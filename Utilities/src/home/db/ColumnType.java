package home.db;

import java.io.IOException;

public class ColumnType {
	private String name ="";
	private String type = "";
	private boolean primaryKey = false;
	private PkCriteria pkCrit = null;
			
	public ColumnType(String name, boolean primaryKey) {
		this.name = name;
		this.primaryKey = primaryKey;
	}
	
	public ColumnType INT() throws IOException{
		checkIfTypeIsSet();
		this.type = "INT";
		return this;
	}
	public ColumnType TinyInt() throws IOException{
		checkIfTypeIsSet();
		this.type = "TINYINT";
		return this;
	}
	public ColumnType SmallInt() throws IOException{
		checkIfTypeIsSet();
		this.type = "SMALLINT";
		return this;
	}
	public ColumnType BigInt() throws IOException{
		checkIfTypeIsSet();
		this.type = "BIGINT";
		return this;
	}
	public ColumnType VarChar(int precision) throws IOException{
		checkIfTypeIsSet();
		this.type = "VARCHAR("+precision+")";
		return this;
	}
	public ColumnType TimeStamp() throws IOException{
		checkIfTypeIsSet();
		this.type = "TIMESTAMP";
		return this;
	}
	public ColumnType Boolean() throws IOException{
		checkIfTypeIsSet();
		this.type = "BOOLEAN";
		return this;
	}
	public ColumnType setPkCriteria(PkCriteria crt) throws IOException{
		
		this.pkCrit = crt;
		return this;
	}
	public String value() throws IOException{
		if(this.type.length() == 0) {
			throw new IOException("You must enter a type for the cloumn type. I.e: INT, Boolean");
		}
		if (primaryKey) {
			if(pkCrit == null) {
				throw new IOException("This is a primary key. Please set the PkCriteria value");
			}
			return name + " " + type + " " + pkCrit.value();
		}
		return name + " " + type;
	}
	private void checkIfTypeIsSet() throws IOException {
		if (this.type.length() > 0) {
			throw new IOException("you can only set 1 type");
		}
	}
}
