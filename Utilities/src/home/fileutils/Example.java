package home.fileutils;

import java.io.File;
import java.util.zip.ZipException;

public class Example {
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		zip();
		unzip();
	}

	private static void unzip() throws ZipException, Exception{
		UnZip unZip = new UnZip();
		unZip.addUnzipFile("c:\\temp\\ttst2.zip.encZip")
		.password("12345")
		.backgroundProcess(true)
		.addZipListener(new ZipFileListener() {

			@Override
			public void percentProcessed(int percent) {
				System.out.println(percent);
			}

			@Override
			public void fileProcessed(String file) {
				System.out.println(file);
			}
		})
		.createDistFolder(true)
		.addUnzipDestFolder( "c:\\temp\\test")		
		.unZipIt();		
	}

	private static void zip() throws Exception{
		Zip z = new Zip("c:\\temp\\ttst2.zip", false);

		File dir = new File("c:\\jboss\\PAB");
		z.addDirectory(dir);
		z.addFile(new File("c:\\temp\\testKey.jks"));
		z.addFile(new File("c:\\temp\\Utils.jar"));
		z.addDirectory(new File("C:\\jboss\\logs"));
		z.addDirectory(new File("C:\\jboss\\commons-exec-1.3-src"));

		z.addZipListener(new ZipFileListener() {

			@Override
			public void fileProcessed(String file) {
				System.out.println("Processing : " + file);				
			}

			@Override
			public void percentProcessed(int percent) {
				System.out.println("Percent: " + percent);

			}
		});

		z.backgroundProcess(true); 
		z.setPassword("12345");
		z.compress();
		System.out.println("running end");
	}
}
