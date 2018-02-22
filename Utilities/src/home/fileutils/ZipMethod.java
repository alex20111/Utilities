package home.fileutils;

public enum ZipMethod {
	Deflated(8),Stored(0);

	private int method = 5;
	
	private ZipMethod(int method){
		this.method = method;
	}
	public int getMethod(){
		return this.method;
	}
}
