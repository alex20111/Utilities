package home.logger.rest;

public enum RemoteLogLevel {
	DEBUG(3), INFO(7), ERROR(9);
	
	
	private int weight = 3;
	
	private RemoteLogLevel(int weight) {
		this.weight = weight;
	}
	
	public int getWeight() {
		return this.weight;
	}
}
