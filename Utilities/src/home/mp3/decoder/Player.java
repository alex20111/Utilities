package home.mp3.decoder;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Player {

	public static void main(String[] args) throws IOException {
		System.out.println("start");
		Decoder d = new Decoder();
		
		String mp3 = "/home/pi/piClock/mp3/06 Cheap Thrills.mp3";		

		FileInputStream in = new FileInputStream(mp3);
		BufferedInputStream bin = new BufferedInputStream(in, 128 * 1024);
		
		d.play("06 Cheap Thrills.mp3", bin);
		
		System.out.println("end");

	}

}
