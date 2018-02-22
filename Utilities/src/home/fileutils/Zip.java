package home.fileutils;

import home.crypto.Encryptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class Zip {
	
	private ZipOutputStream zos;
	private List<File> filesToZip = new ArrayList<File>();
	private List<File> dirsToZip = new ArrayList<File>();
	private File zipFile;
	private String password ="";
	
	private List<ZipFileListener> listeners = new ArrayList<ZipFileListener>();
	
	private boolean append = false;
	private boolean backGroundProcess = false;
	
	private Thread backgroundZip;
	
	public Zip(){}
	/**
	 * create new zip file. If one of the same name exist, it will be deleted.
	 * @param fileName - Name of the zip file
	 * @throws FileNotFoundException
	 */
	public Zip(String fileName) throws FileNotFoundException{
		setZipFile(fileName, false);
	}
	/**
	 * create/modify new zip file. 
	 * @param append - If the append is set to true, it will add the compressed files to the existing ZIP (if exist)
	 * @param fileName - Name of the zip file
	 * @throws FileNotFoundException
	 */
	public Zip(String fileName, boolean append) throws FileNotFoundException{
		setZipFile(fileName, append);
	}
	/**
	 * set the name of the zip 
	 * @param file
	 * @param append
	 * @throws FileNotFoundException
	 */
	public Zip setZipFile(String file, boolean append) throws FileNotFoundException{
		if (file == null){
			throw new FileNotFoundException("File empty");
		}
		if (file.indexOf(".zip") == 0){
			file += ".zip";
		}				
		
		this.append = append;
		zipFile = new File(file);
		
		return this;

	}	
	/**
	 * add a file to the zip.
	 * 
	 * @param file - file to be zipped
	 * @throws ZipException
	 */
	public Zip addFile(File file) throws ZipException{
		
		if (!file.isFile()){
			throw new ZipException("Not a file");
		}
		filesToZip.add(file);
		
		return this;
		
	}
	/**
	 * add a list of files to zip.
	 * @param files - List of files to be zipped
	 * @throws ZipException
	 */
	public Zip addFiles(List<File> files) throws ZipException{
		for(File f : files){
			if (!f.isFile()){
				throw new ZipException("the list contains a directory :  " + f.getAbsolutePath());
			}
		}
		filesToZip.addAll(files);	
		
		return this;
	}
	/**
	 * add a directory and sub directories to the zip
	 * @param dir
	 * @throws ZipException
	 */
	public Zip addDirectory(File dir) throws ZipException{
		if (!dir.isDirectory()){
			throw new ZipException("Not a Directory");
		}
		dirsToZip.add(dir);
		return this;
	}
	/**
	 * add a list of directories and sub directories to the zip.
	 * @param dirs
	 * @throws ZipException
	 */
	public Zip addDirectories(List<File> dirs) throws ZipException{
		for(File f : dirs){
			if (!f.isDirectory()){
				throw new ZipException("the list contains file that is not a directory :  " + f.getAbsolutePath());
			}
		}
		dirsToZip.addAll(dirs);
		
		return this;
	}
	public void compress() throws  Exception{
		compress(CompressionLevel.Normal, ZipMethod.Deflated);
		
	}
	public void compress(CompressionLevel level) throws Exception{
		compress(level, ZipMethod.Deflated);
		
	}
	public void compress(final CompressionLevel level, final ZipMethod method) throws Exception{

		//if we need to put the process into background
		if (backGroundProcess){
			backgroundZip = new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					try {
						compressProcess( level,  method);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			backgroundZip.start();
		}
		else
		{
			compressProcess( level,  method);
		}
	}
	
	private void compressProcess(CompressionLevel level, ZipMethod method) throws Exception{
		Map<File, List<String>> dirList = new HashMap<File, List<String>>();
		byte[] buffer = new byte[1024];
		int percent = 0;
		int total = 0;

		//build directory list if any provided.
		if (dirsToZip.size() > 0){
			fireEvent("Building directory list.",0);
			for(File dir : this.dirsToZip){

				int cut = (dir.getParent().lastIndexOf(String.valueOf(File.separatorChar)) == (dir.getParent().length() - 1) ?
						dir.getParent().length() : dir.getParent().length() + 1);

				List<String> dirFileList = generateFileList(dir,cut);
				dirList.put(dir, dirFileList);
				total += dirFileList.size(); //add the totals.
			}
		}

		if (append && (dirsToZip.size() > 0 || filesToZip.size() > 0)){ //append to file
			
			fireEvent("Adding files",0);
			File tempFile = File.createTempFile(zipFile.getName(), null);
			// delete it, otherwise you cannot rename your existing zip to it.
			tempFile.delete();
			
			boolean renameOk=zipFile.renameTo(tempFile);
			if (!renameOk)
			{
				throw new RuntimeException("could not rename the file "+zipFile.getAbsolutePath()+" to " +tempFile.getAbsolutePath() );
			}
			

			ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
			zos = new ZipOutputStream(new FileOutputStream(zipFile));

			ZipEntry entry = zin.getNextEntry();
			//
			while (entry != null) {
				String name = entry.getName(); //get name of the entry from the temp file

				boolean notInFiles = true;
				//verify if adding files exist in the zip. If yes, do not add them here..
				//add them later on
				for (File f : filesToZip) { 
					if (f.getName().equals(name)) {
						notInFiles = false;
						break;
					}
				}

				boolean notInDirs = true;
				//check if the file is in the dir structure if not found in files.
				if (notInFiles){
					for(Map.Entry<File, List<String>> dr : dirList.entrySet()){
						for(String str : dr.getValue()){
							if (str.equals(name)) {
								notInDirs = false;
								break;
							}
						}
					}
				}
				if (notInDirs && notInFiles){					
					// Add ZIP entry to output stream.
					zos.putNextEntry(new ZipEntry(name));
					zos.setMethod(method.getMethod());
					zos.setLevel(level.getLevel()); //0 to 9
					// Transfer bytes from the ZIP file to the output file
					int len;
					while ((len = zin.read(buffer)) > 0) {
						zos.write(buffer, 0, len);
					}
				}
				entry = zin.getNextEntry();
			}
			// Close the streams        
			zin.close();
			tempFile.delete();
		}
		else
		{
			zos = new ZipOutputStream(new FileOutputStream(zipFile));
		}

		//add files to zip
		if (filesToZip.size() > 0){		
			total += filesToZip.size(); //add the totals.
			for(File file : this.filesToZip){
				ZipEntry ze = new ZipEntry(file.getName()); 
				zos.putNextEntry(ze);				
				zos.setMethod(method.getMethod());
				zos.setLevel(level.getLevel()); //0 to 9

				FileInputStream in = new FileInputStream(file);
				percent++;
				fireEvent(file.getName(), ( percent * 100) / total);
				
				
				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}

				in.close();
			}
		}		

		//add dirs
		for(Map.Entry<File, List<String>> dr : dirList.entrySet()){

			for(String fileStr : dr.getValue()){
				ZipEntry ze = new ZipEntry(fileStr); 
				zos.putNextEntry(ze);				
				zos.setMethod(method.getMethod());
				zos.setLevel(level.getLevel()); //0 to 9

				FileInputStream in = new FileInputStream(dr.getKey().getParent().toString() + File.separatorChar + fileStr);

				percent++;
				fireEvent(fileStr,( percent * 100) / total);
				
				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}

				in.close();
			}
		}	
		
		
		zos.closeEntry();
		//remember close it
		zos.close();
		
		
		if (password != null && password.length() > 0){
			

			fireEvent("Encrypting file...", ( percent * 100) / total);
			Encryptor.encryptFile(zipFile.getCanonicalPath(), zipFile.getCanonicalPath()+ ".encZip", password.getBytes("utf-8"));
			
			zipFile.delete();
		}	

		fireEvent("Done!", ( percent * 100) / total);
	}
   /**
     * Traverse a directory and get all files,
     * and add the file into fileList  
     * @param node file or directory
	 * @throws IOException 
     */
	private List<String> generateFileList(File dir, int cut) throws IOException{
		
		List<String> files = new ArrayList<String>();
		
        File[] list = dir.listFiles();

        if (list == null) return files;

        for ( File f : list ) {
            if ( f.isDirectory() ) {
            	files.addAll(generateFileList( f, cut));
            }
            else {
            	files.add(f.getCanonicalPath().substring(cut , f.getCanonicalPath().length()));
            }
        }
        return files;
	}

	public Zip backgroundProcess(boolean background){
		this.backGroundProcess = background;
		
		return this;
	}
	
	public synchronized Zip addZipListener( ZipFileListener l ) {
		listeners.add( l );
		
		return this;
	}
	public synchronized Zip removeZipListener( ZipFileListener l ) {
		listeners.remove( l );
		return this;
	}
	private synchronized void fireEvent(String fileEvent, int percent) {

		Iterator<ZipFileListener> _listeners = listeners.iterator();
		while( _listeners.hasNext() ) {
			ZipFileListener z =  (ZipFileListener) _listeners.next() ;
			z.fileProcessed( fileEvent );
			z.percentProcessed( percent );
		}
	}
	public Zip setPassword(String password){
		this.password = password;
		return this;
	}
	
}
