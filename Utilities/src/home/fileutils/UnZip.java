package home.fileutils;

import home.crypto.Encryptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class UnZip {

	private File zipFile;
	private File destFolder;
	private boolean createDistFolder = false; 
	private boolean backGroundProcess = false;
	private List<ZipFileListener> listeners = new ArrayList<ZipFileListener>();
	
	private String password = "";
	List<String> fileList;

	
	public UnZip(){}
	public UnZip(String file, String destFolder) throws ZipException{
		addUnzipFile(file);
		addUnzipDestFolder(destFolder);
	}
	/**
	 * 
	 * @param file
	 * @param destFolder
	 * @throws ZipException
	 */
	public UnZip addUnzipFile(String file) throws ZipException{	
		
		this.zipFile = new File(file);
		
		if (zipFile == null || !zipFile.exists())
		{
			throw new ZipException("Zip File not found: " + zipFile);
		}
		return this;
	}
	/**
	 * Add destination folder.
	 * @param destFolder
	 * @throws ZipException
	 */
	public UnZip addUnzipDestFolder(String destFolder) throws ZipException{			
		
		this.destFolder = new File(destFolder);		 
		 
		if (!this.destFolder.exists() && !createDistFolder)
		{			
			throw new ZipException("Destination folder does not exist : " + destFolder + ". Set createDistFolder(true) if you want the folder to be created automatically.");
		}
		else if (!this.destFolder.exists() && createDistFolder){
			this.destFolder.mkdir();
		}
		return this;
	}
	/**
	 * Unzip it
	 * @param zipFile input zip file
	 * @param output zip file output folder
	 * @throws Exception 
	 */
	public void unZipIt() throws Exception{
		
		if (zipFile == null){
			throw new ZipException("No zip file, please set a Zip file");
		}
		if (destFolder == null){
			throw new ZipException("No output folder set, please set a output folder");
		}
		if (zipFile.getName().contains("encZip") && this.password.length() == 0){
			throw new ZipException("file is password protected. please provide password");
		}
		
		
				
		if (backGroundProcess){
			Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
			    public void uncaughtException(Thread th, Throwable ex) {
			       try {
					throw new Exception(ex);
				} catch (Exception e) {
					e.printStackTrace();
				}
			    }
			};
			
			
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						processUnzip();
					} catch (Exception e) {	
						throw new RuntimeException(e);
					}
					
				}
			});
			t.setUncaughtExceptionHandler(h);
			t.start();
		}
		else
		{
			processUnzip();
		}
	
		System.out.println("Done");
	}  
	private void processUnzip() throws Exception{
		int percent = 0;
		int total = 0;		
		
		fireEvent("Starting...",0); 
		byte[] buffer = new byte[1024];

		File tmpFile = null;
		if (password != null && password.length() > 0){
			tmpFile = new File(zipFile.getCanonicalPath() + ".tmp");
			Encryptor.decryptFile(zipFile.getCanonicalPath(), tmpFile.getCanonicalPath(), password.getBytes("utf-8"));
			zipFile = tmpFile;
		}		
		
		ZipFile f = new ZipFile(zipFile);		
		total = f.size();
		f.close();
		//get the zip file content
		FileInputStream fis = new FileInputStream(zipFile);
		ZipInputStream zis = new ZipInputStream(fis);
		
		//get the zipped file list entry
		ZipEntry ze = zis.getNextEntry();

		while(ze!=null){

			String fileName = ze.getName();
			
			File newFile = new File(destFolder + File.separator + fileName);
			percent ++;
			fireEvent(newFile.getAbsolutePath(), (percent * 100) / total); 
			
			//create all non exists folders			
			if (ze.isDirectory()){
				newFile.mkdirs();				
			}else{			
				new File(newFile.getParent()).mkdirs();
				
				FileOutputStream fos = new FileOutputStream(newFile);             

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();  
			}			
			 
			ze = zis.getNextEntry();
		}

		fis.close();
		zis.closeEntry();
		zis.close();

		if (tmpFile != null){
			tmpFile.delete();
		}
		fireEvent("Done!", (percent * 100) / total); 
	}
	public UnZip password(String password){
		this.password = password;
		return this;
	}
	/**
	 * 
	 * @param create
	 */
	public UnZip createDistFolder(boolean create){
		this.createDistFolder = create;
		return this;
		
	}
	public synchronized UnZip addZipListener( ZipFileListener l ) {
		listeners.add( l );
		
		return this;
	}
	public synchronized UnZip removeZipListener( ZipFileListener l ) {
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
	public UnZip backgroundProcess(boolean background){
		this.backGroundProcess = background;
		
		return this;
	}
	
}
