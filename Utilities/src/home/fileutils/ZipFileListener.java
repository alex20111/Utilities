package home.fileutils;

public interface ZipFileListener {

	public void fileProcessed(String file);
	public void percentProcessed(int percent);
}
