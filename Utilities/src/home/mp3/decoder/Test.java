package home.mp3.decoder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;



public class Test {

	private static Decoder decoder;
	public static void main(String[] args) throws IOException {

		decoder = new Decoder();
		
		play(new File("c:\\users\\ADMIN\\desktop\\Shawn_Mendes_-_Se_orita_ft_Camila_Cabello_.mp3"));

		System.out.println("Finished");
		
	}
	 private static void play(File file) throws IOException {
//	        player.setCurrentFile(file);
//	        stop = false;
	        if (!file.getName().endsWith(".mp3")) {
	            return;
	        }
	        System.out.println("playing: " + file);
	        FileInputStream in = new FileInputStream(file);
	        BufferedInputStream bin = new BufferedInputStream(in, 128 * 1024);
	        decoder.play(file.getName(), bin);
//	        currentFile = null;
	    }
}
