package home.misc;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

public class ImgBase64 {

	
	public String imgToBase64( BufferedImage bi ) throws IOException{
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ImageIO.write(bi, "jpg", output);
		return DatatypeConverter.printBase64Binary(output.toByteArray());
	}
	
}
