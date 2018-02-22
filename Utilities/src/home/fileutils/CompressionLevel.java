package home.fileutils;

public enum CompressionLevel {
NoComp(0),Fastest(1),Fast(3),Normal(5),Best(7),Maximum(9);

	private int level = 5;
	private CompressionLevel(int level){
		this.level = level;
	}
	public int getLevel(){
		return this.level;
	}
}
