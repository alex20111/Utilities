package home.fileutils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Testbytes {

	public static void main(String[] args) {
		File f = new File("c:\\dev\\PICLOCK-BACKUP.zip");


		//		System.out.println("File lenght: " + f.length());
		//		
		//		System.out.println("File ut: " + FileUtils.fileSizePretty(f.length()));
		//		
		//		System.out.println("File bytes: " + FileUtils.fileSizeRaw(f.length(), FileSize.BYTES));
		//		System.out.println("File Kb: " + FileUtils.fileSizeRaw(f.length(), FileSize.KB));
		//		System.out.println("File mb: " + FileUtils.fileSizeRaw(f.length(), FileSize.MB));
		//		System.out.println("File gb: " + FileUtils.fileSizeRaw(f.length(), FileSize.GB));
		//		System.out.println("File tb: " + FileUtils.fileSizeRaw(f.length(), FileSize.TB));
		//		System.out.println("File pb: " + FileUtils.fileSizeRaw(f.length(), FileSize.PB));

		long length = f.length();

		double size = length;

		int result = (int)(size / 1024 / 1024);

		System.out.println("in file: " + result);

		if (result == 6) {
			System.out.println("result 6: " + f.getPath());
			//try to see if we already appended the file with a number 1st
			boolean exist = true;
			int idx = 0;
			File newFile = null;
			while(exist) {				
				newFile = new File(f.getPath() + "." + idx);
				exist = newFile.exists();
				if(exist) {
					idx++;
				}
				System.out.println("exist: " + exist + "  filename: " + newFile.getPath());
			}
			
			System.out.println("New file to create: " + newFile.getPath());
			//copy currect file into new file
			try {
				Files.copy(f.toPath(), newFile.toPath());
				
				Files.delete(f.toPath());
				
				//create empty base on the original file
				boolean c = f.createNewFile();
				
				System.out.println("File created: " + c);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		}

	}

}
