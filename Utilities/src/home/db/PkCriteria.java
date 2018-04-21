package home.db;

import java.io.IOException;

public class PkCriteria {
	private String strategy = "";
	
	public PkCriteria autoIncrement() {
		this.strategy = "AUTO_INCREMENT";
		return this;
	}
	
	public String value() throws IOException {
		if(this.strategy.length() == 0) {
			throw new IOException("Strategy needed for Primary Key");
		}
		
		return this.strategy;
	}
}
