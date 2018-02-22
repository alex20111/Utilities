package home.fileutils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Class created to handle files writes. 
 * 
 */
public class FileUtils
{
	/**
	 * check if a file exist based on a string.
	 */
	public static boolean fileExist(String file)
	{
		return new File(file).exists();
	}
	/**
	 * Erase the file based on a string.
	 * @throws IOException 
	 */
	public static boolean eraseFile(String location) throws IOException
	{
		boolean success = false;
		
		File file = new File(location);
		
		if (file.exists() && file.isFile())
		{
			success = file.delete();
		}
		else if (!file.exists())
		{
			success = true;
		}
		else if (file.isDirectory()){
			throw new IOException("Cannot erase directory: " + file.getAbsolutePath() );
		}
		
		return success;
	}
	/**
	 * 
	 * @param location
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static String writeObjectToFile(String location, Object obj, boolean append) throws IOException
	{
		File file = new File(location);		
		
		//if file dosen't exit, write it. Or if we need to append and the file exist.
		if (!file.exists() || (file.exists() && file.isFile() && append) )
		{
			if (append && file.exists() && file.isFile()){
				FileOutputStream fout = new FileOutputStream(file, append);
				AppendingObjectOutputStream oos = new AppendingObjectOutputStream(fout); 
				oos.writeObject(obj);
				oos.close();
			}
			else{
				FileOutputStream fout = new FileOutputStream(file);
				ObjectOutputStream oos = new ObjectOutputStream(fout); 
				oos.writeObject(obj);
				oos.close();				
			}
		}
		else if (file.exists() && file.isFile() && !append)
		{
			throw new IOException("file " + file.getAbsolutePath() + " exist and appending boolean is not set to true, please delete");
		}
		else 
		{
			throw new IOException("You need to provide a file name to write to. File provided " + file.getAbsolutePath() );	
		}
		return file.getAbsolutePath();
	}
	/**
	 * Write a list of objects to a specified location.
	 * @param location
	 * 			- location to store the file
	 * @param objects
	 * 		- objects to write
	 * @throws Exception
	 */
	public static String writeObjectListToFile(String location, List<Object> objects) throws IOException 
	{
		File file = new File(location);

		if (file.exists() && file.isFile())
		{
			throw new IOException("file " + file.getAbsolutePath() + " exist , please delete.");
		}
				
		FileOutputStream fout = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fout);  
		for (Object obj : objects)
		{
			oos.writeObject(obj);
		}
		oos.close();
		fout.close();
		
		return file.getAbsolutePath();
	}
	/**
	 * 
	 * @param location
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws Exception
	 */
	public static List<Object> readObjectListFromFile(String location) throws IOException, ClassNotFoundException
	{
		File file = new File(location);
		List<Object> objs = new ArrayList<Object>();
		if (file.exists() && file.isFile())
		{
			FileInputStream fin = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fin);
			try{
				while (fin.available() != -1) {
					//Read object from file
					objs.add(ois.readObject());
				}	
			}catch(EOFException eof)
			{
				ois.close();
			}
			finally
			{
				if (ois != null)
					ois.close();
				
				if (fin != null)
					fin.close();
			}
		}else{
			throw new IOException("File " + file.getAbsolutePath() + " does not exist or is not a file.");
		}
		
		return objs;
	}
	/**
	 * 
	 * @param location
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws Exception
	 */
	public static Object readObjectFromFile(String location) throws IOException
, ClassNotFoundException
	{
		File file = new File(location);
		Object obj = null;
		if (file.exists() && file.isFile())
		{
			obj = new Object();
			FileInputStream fin = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fin);
			try{
				//Read object from file
				obj = ois.readObject();

			}catch(EOFException eof)
			{
				ois.close();
			}
			finally
			{
				if (ois != null)
				{
					ois.close();
				}
				if (fin != null)
				{
					fin.close();
				}
			}
		}
		else{
			throw new IOException("File " + file.getAbsolutePath() + " does not exist or is not a file.");
		}
		return obj;
	}
	public static File  writeStringToFile(String location, String text, boolean append) throws IOException
	{
		return writeStringToFile(location, text, append,"UTF-8"); 
	}
	/**
	 * Write a simple text to file.
	 * @param location
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public static File  writeStringToFile(String location, String text, boolean append,String charset) throws IOException
	{
		File file = new File(location);

		if (file.exists() && file.isFile() && !append)
		{
			throw new IOException("file " + file.getAbsolutePath() + " exist and appending boolean is not set to true, please delete");
		}		

		if (charset == null || charset.length() == 0){
			charset = "UTF-8";
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
			    new FileOutputStream(file, append), charset));
		bw.write(text);
		bw.newLine();

		bw.flush();
		bw.close();
	

		return file;
	}
	public static String readFileToString(String location) throws IOException 
	{
		return readFileToString(location, "UTF-8");
	}
	/**
	 * 
	 * @param location
	 * @return
	 * @throws Exception
	 */
	public static String readFileToString(String location, String charset) throws IOException 
	{
		StringBuilder  stringBuilder = new StringBuilder();

		File file = new File(location);

		if (file.exists() && file.isFile()){

			if (charset == null || charset.length() ==0){
				charset = "UTF-8";
			}
			
			BufferedReader reader = new BufferedReader( new InputStreamReader(
                    new FileInputStream(file), charset));

			String         line = null;

			while( ( line = reader.readLine() ) != null )
			{
				stringBuilder.append( line );
			}
			reader.close();
		}
		else
		{
			throw new IOException("File " + file.getAbsolutePath() + " does not exist or is not a file.");
		}

		return stringBuilder.toString();

	}
	/**
	 * Read a file containing strings to array.
	 * @param location
	 * @return
	 * @throws Exception
	 */
	public static List<String> readFileToArray(String location) throws Exception 
	{
		File file = new File(location);

		List<String> string = new ArrayList<String>();

		if (file.exists() && file.isFile()){

			BufferedReader reader = new BufferedReader( new FileReader (location));
			String         line = null;
			
			while( ( line = reader.readLine() ) != null ) 
			{
				string.add(line);
			}
			reader.close();
		}
		else
		{
			throw new IOException("File " + file.getAbsolutePath() + " does not exist or is not a file.");
		}
		return string;
	}
	/**
	 * Write a list of objects to a specified location.
	 * @param location
	 * 			- location to store the file
	 * @param objects
	 * 		- objects to write
	 * @throws Exception
	 */
	public static boolean writeObjListToFileUsingCompression(String fileLoc, List<Object> objects) throws IOException
	{
		File file = new File(fileLoc);
		
		if(file.exists() && file.isFile())
		{
			throw new IOException("file " + file.getAbsolutePath() + " exist, please delete");
		}

		ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file)));

		for (Object obj : objects)
		{
			oos.writeObject(obj);
		}

		oos.flush();
		oos.close();
		
		return true;
	}
	/**

	 */
	public static List<Object> ReadObjListFromCompressedFile(String fileLoc) throws Exception 
	{
		File file = new File(fileLoc);

		List<Object> objs = new ArrayList<Object>();

		if (file.exists() && file.isFile()){

			FileInputStream fin = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(fin));

			try{
				while (fin.available() != -1) {
					//Read object from file
					objs.add(ois.readObject());
				}	
			}catch(EOFException eof)
			{
				ois.close();
			}
			finally
			{
				if (ois != null)
					ois.close();

				if (fin != null)
					fin.close();
			}
		}
		else
		{
			throw new IOException("File " + file.getAbsolutePath() + " does not exist or is not a file.");
		}
		return objs;
	}
	
	/**
	 * pretty format the file size.
	 * 
	 * @param totalBytes
	 * @return
	 */
	public static String fileSize(long totalBytes)
	{
		
		int divisions = 0;
		double size = totalBytes;
		while( size > 1024 )
		{
			size /= 1024;
			divisions++;
		}
		
		StringBuilder sb = new StringBuilder();
		switch (divisions) {
		case 0:
			sb.append(Math.round(size)).append(" bytes");
			break;
		case 1:
			sb.append(Math.round(size)).append(" KB");
			break;
		case 2:
			sb.append(Math.round(size)).append(" MB");
			break;
		case 3:
			sb.append(Math.round(size)).append(" GB");
			break;
		case 4:
			sb.append(Math.round(size)).append(" TB");
			break;
		default:
			sb.append(Math.round(size)).append(" PB");
		}
			
		return sb.toString();
	}
	/**
	 * read a string randomly starting from a specified position.
	 * @param filePath
	 * @param position
	 * @return
	 * @throws IOException
	 */
	public static String readRandomStringFromFile(String filePath, int position) throws IOException { 

		File file = new File(filePath);

		StringBuilder sb = new StringBuilder();

		if (file.exists() && file.isFile()){
			RandomAccessFile rfile = new RandomAccessFile(file, "r"); 

			rfile.seek(position);		

			String line = null;
			while( (line  = rfile.readLine()) != null){
				sb.append(line);
			}				
			rfile.close(); 
		}
		else {
			throw new IOException("File " + file.getAbsolutePath() + " does not exist or is not a file.");
		}
		return sb.toString(); 

	} 

	public static void writeToRandomAccessFile(String filePath, String data, int position)   throws IOException { 

        RandomAccessFile file = new RandomAccessFile(filePath, "rw"); 
        file.seek(position); 
        file.writeChars(data); 
        file.close(); 
    } 

	/**
	 * Get all the files from a directory recursively
	 * 
	 * @param path - Path to dir
	 * @param withDirName - add the directory as FILE . 
	 * @return
	 */
	public static List<File> getFilesFromDir( File path , boolean withDirName) 
	{   
		List<File> files = new ArrayList<File>();
		
        File[] list = path.listFiles();

        if (list == null) return files;

        for ( File f : list ) {
            if ( f.isDirectory() ) {
            	if (withDirName){
            		files.add(f);
            	}
            	files.addAll(getFilesFromDir( f , withDirName));
            }
            else {
            	files.add(f);
            }
        }
        return files;
    }
	/**
	 * get the size of the directory in bytes.
	 * @param directory
	 * @return
	 * @throws IOException 
	 */
	public static long folderSize(File directory) throws IOException {

		long length = 0;
		if (directory.isDirectory()){

			for (File file : directory.listFiles()) {
				if (file.isFile())
					length += file.length();
				else
					length += folderSize(file);
			}
		}else{
			throw new IOException("File " + directory.getAbsolutePath() + "is not a directory");
		}
		return length;
	}
	
}
 class AppendingObjectOutputStream extends ObjectOutputStream {

	  public AppendingObjectOutputStream(OutputStream out) throws IOException {
	    super(out);
	  }

	  @Override
	  protected void writeStreamHeader() throws IOException {
	    // do not write a header, but reset:
	    // this line added after another question
	    // showed a problem with the original
	    reset();
	  }
	}