package home.db;

import home.misc.StringUtl;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * This class manages connection to the DB
 * 
 */
public class DBConnection {

	private Connection conn 	= null;
	private PreparedStatement pstmt 	= null;
	private ResultSet rs 			= null;

	private String query = "";
	private String whereStatment = "";

	private Map<String, Object> parameters = null;
	private Map<String, Object> updateParam = null;
	
	private boolean buildAddQuery = false; //specify if its the 1st time the addParamenter is used
	private boolean buildUpdateQuery = false;
	private boolean addBatchfirstValue = true;

	public String showQuery(){
		if (pstmt != null){
			return pstmt.toString();
		}
		return "No Query";
	}
	/**
	 * constructor
	 * ex: DBConnection con = new DBConnection(url, user,pass,driver);
	 * 
	 * @param url - URL of the database
	 * @param dbUser - User of the database
	 * @param dbPassword - Password for the database
	 * @param dbClass - What kind of database. H2 and Mysql supported.
	 * 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public DBConnection(String url, String dbUser, String dbPassword, DbClass dbClass) throws SQLException, ClassNotFoundException  {
		Class.forName(dbClass.getDriver());
		this.conn = DriverManager.getConnection(url, dbUser, dbPassword);

	}
	/***
	 * Build the ADD(INSERT) query to facilitate the SQL building. Also use to add in batch
	 * EX: int primaryKey = con.buildAddQuery("TableNameToAddTO")		
	 *	  .setParameter(Column name , Value) 
	 *    .setParameter(Column name , Value)
	 *	  .add(); //execute add
	 *
	 * EX add batch:
	 *  con.buildAddQuery(TableNameToAddTO);
	 *	 
	 *	for(int i = 0; i < 3 ; i ++){
	 *		con.setParameter(column, column value)
	 *		.addToBatch();
	 *	}
	 * 	
	 *	con.executeBatchQuery();
	 * 
	 * @param table	- The table name to build the query
	 * @return
	 * @throws SQLException
	 */
	public DBConnection buildAddQuery(String tableName) throws SQLException{
		this.query = "INSERT INTO " + tableName;
		buildAddQuery = true;
		addBatchfirstValue = true;
		
		return createSelectQuery(query);
	}
	/***
	 * Build the UPDATE query to facilitate the SQL building.
	 * EX: con.buildUpdateQuery("Table name to update")
	 *	.setParameter(column name, columnValue);  //column value is an object, so it can be a string or boolean or int or float ect...
	 *  .setParameter(column name, true);	 			
	 *	.addUpdWhereClause("WHERE column name = :userId OR column name = :userId2 ", valueForUserId, valueForUserId2)  
	 *	.update();
	 * 
	 * @param table	- The table name to build the query
	 * @return
	 * @throws SQLException
	 */
	public DBConnection buildUpdateQuery(String tableName) throws SQLException{
		this.query = "UPDATE " + tableName;
		whereStatment = ""; //reset where statement.
		updateParam = null;
		
		buildUpdateQuery = true;
		
		return createSelectQuery(query);
	}
	/**
	 * Create basic query. Basically used as a SELECT or DElete.
	 * EX: String query = "SELECT * FROM tableName where id > :idKey";
     *
	 *	ResultSet rs = con.createQuery(query)
	 *			.setParameter("idKey", 22)
	 *			.getSelectResultSet();
	 *
	 * @param query	- The query.
	 * @return
	 * @throws SQLException
	 */
	public DBConnection createSelectQuery (String query) throws SQLException{

		if (query != null && query.length() > 0){	
			parameters = new LinkedHashMap<String, Object>();
			this.query = query;
		}
		else{
			close();
			throw new SQLException("Query is empty");
		}
		return this;
	}
	/**
	 * 
	 * @param tableName - The name of the new table
	 * @param columns - the columns name and data type. EX: columns.put("id","INT Primary key");
	 * @return
	 * @throws SQLException
	 */
	public DBConnection createTable (String tableName, Map<String, String> columns) throws SQLException{
		
		if (tableName != null && tableName.length() > 0){
			
			if (columns != null && !columns.isEmpty())
			{
				parameters = new LinkedHashMap<String, Object>();
				
				query = "CREATE TABLE " + tableName + " (";
				
				boolean first = true;
				for(Map.Entry<String, String> column : columns.entrySet()){
					
					if (first){
						query += column.getKey() + " " + column.getValue();
						first = false;
					}else{
						query += " , " + column.getKey() + " " + column.getValue();
					}
				}
				
				query += " ) ";
				
				buildStatement(QueryType.SELECT);
				
				pstmt.executeUpdate();
			}
			else
			{
				close();
				throw new SQLException("You need at least 1 column in the table.");
			}
			
		}else{
			close();
			throw new SQLException("Table name needed");
		}
		
		return this;
	}
	/**
	 * Basically delete in batch on 1 parameter.
	 * DELETE FROM table WHERE id IN (?,?,?,?,?,?,?,?)
	 * 
	 * con.deleteInBatch(tableName, column, list of values to check);
	 * 
	 * @param tableName
	 * @param param
	 * @param values
	 * @return
	 * @throws SQLException 
	 */
	public void deleteInBatch(String tableName, String column, List<Object> values) throws SQLException{
	
		StringBuilder delQuery = new StringBuilder("DELETE FROM " + tableName + " WHERE " + column + " IN (");		
						
		parameters = new LinkedHashMap<String, Object>();
		
		boolean first = true;
		for(int i = 0 ; i <  values.size() ; i++){
			if (first){
				delQuery.append("?");
				first = false;
			}else{
				delQuery.append(",?");
			}
			
			parameters.put("batch" + i, values.get(i));
				
			
		}
		delQuery.append(")");

		
		pstmt = conn.prepareStatement(delQuery.toString());		
		addParameters();
		
		pstmt.executeUpdate();		
		
	}
	
	/**
	 * Set the parameter to the query
	 * @param param	- Parameter to add to the query. Must always start with :thentheword 
	 * @param value - The value to the parameter.
	 * @return
	 * @throws SQLException
	 */
	public DBConnection setParameter(String param, Object value) throws SQLException{
		if (query.length() > 0)
		{			
			if (parameters.containsKey(param)){
				throw new SQLException("More than 1 parameter of name: " + param + ". The parameter must be unique");
			}		

			parameters.put(param, value);			

		}else{
			close();
			throw new SQLException("Please create a query");
		}

		return this;
	}
	/**
	 * Build the query statement based on the query type and the parameters.
	 * 
	 * @param type	- Query type to know if we need to get the PK from the query.
	 * @throws SQLException
	 */
	private void buildStatement(QueryType type) throws SQLException{

		if (parameters.size() > 0){			

			Map<String, Integer> orderParams = new LinkedHashMap<String, Integer>();

			for(String key : parameters.keySet()){

				Pattern pattern = Pattern.compile(":"+key+"\\b");
				Matcher m = pattern.matcher(query);
				if (m.find())
				{
					query = query.replaceAll(":"+key+"\\b", "?");
				}
				else{
					close();
					throw new SQLException("Parameter not found in query. Param: " + key);
				}
				orderParams.put(key, m.start());
			}
			sortParameters(orderParams);
		}

		if (type == QueryType.SELECT){
			pstmt = conn.prepareStatement(query);
		}else if (type == QueryType.INSERT){
			pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		}

		//add the parameters.. 
		addParameters();
	}
	
	private void addParameters() throws SQLException{
		//add param if any
		if (parameters.size() > 0){
			int idx = 1;

			for(String key : parameters.keySet()){

				Object value = parameters.get(key);

				if (value == null){
					pstmt.setNull(idx, java.sql.Types.NULL);
				}else{

					if (value instanceof Integer){	
						pstmt.setInt(idx,(Integer)value );
					}else if(value instanceof String){
						pstmt.setString(idx, (String)value);
					}else if(value instanceof Double){
						pstmt.setDouble(idx, (Double)value);
					}else if(value instanceof Float){
						pstmt.setFloat(idx, (Float)value);
					}else if(value instanceof Date){
						pstmt.setTimestamp(idx , new java.sql.Timestamp(((Date) value).getTime()));
					}else if(value instanceof Long){
						pstmt.setLong(idx, (Long)value);
					}else if(value instanceof Boolean){
						pstmt.setBoolean(idx, (Boolean)value);
					}else if(value instanceof BigDecimal){
						pstmt.setBigDecimal(idx, (BigDecimal)value);
					}else if(value instanceof Short){
						pstmt.setShort(idx, (Short)value);
					}else if(value instanceof Byte){
						pstmt.setByte(idx, (Byte)value);
					}else if(value instanceof Byte[]){
						pstmt.setBytes(idx, (byte[])value);
					}else if(value instanceof Blob){
						pstmt.setBlob(idx, (Blob) value);
					}else{
						close();
						throw new SQLException("Parameter not valid --> " + value);
					}
				}
				idx++;
			}
		}	
	}
	/**
	 * Close the database connection.
	 * @throws SQLException
	 */
	public void close() throws SQLException{
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//	        	log.error("DB close error:" + e.getMessage());
		}
		try{
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//	        	log.error("DB close error:" + e.getMessage());
		}
		try{   
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//	        	log.error("DB close error:" + e.getMessage());
		}			
		
		//reset all other variables
		query = "";
		whereStatment = "";
		parameters = null;
		updateParam = null;		
		buildAddQuery = false; //specify if its the 1st time the addParamenter is used
		buildUpdateQuery = false;
		addBatchfirstValue = true;
		
	}

	public ResultSet getSelectResultSet() throws SQLException{

		buildStatement(QueryType.SELECT);

		rs = pstmt.executeQuery();

		return this.rs;		
	}
	/**
	 * Called to Update the query information to the database.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public int update() throws SQLException{

		if (!StringUtl.containsIgnoreCase(query, "update")){
			close();
			throw new SQLException("Error in QUERY. The UPDATE Method is only used when updating a new record on a table.");
		}

		if (parameters.size() > 0 && buildUpdateQuery){

			StringBuilder tempQuery = new StringBuilder(query);	
			
			boolean first = true;
			for(String column : parameters.keySet()){
				if (first){
					first = false;
					tempQuery.append(" SET " + column + " = " + ":"+column);
				}
				else{
					tempQuery.append( ", " + column + " = " + ":"+column);
				}
			}

			tempQuery.append(" " + whereStatment);			
			
			query = tempQuery.toString();
			
			//add the where parameters to the parameters.
			if (updateParam != null && updateParam.size() > 0){
				parameters.putAll(updateParam);
			}			
		}

		buildStatement(QueryType.SELECT);

		buildUpdateQuery = false;

		return pstmt.executeUpdate();
	}
	/**
	 * Called to delete the query information to the database.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public int delete() throws SQLException{

		buildStatement(QueryType.SELECT);

		return pstmt.executeUpdate();
	}
	/**
	 * Called to add the query information to the database.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public int add() throws SQLException{
		
		if (!StringUtl.containsIgnoreCase(query, "insert")){
			close();
			throw new SQLException("Error in QUERY. The ADD Method is only used when inserting a new record on a table.");
		}
		
		int pk = -1;
		
		if (parameters.size() > 0 && buildAddQuery){
			
			StringBuilder tempQuery = new StringBuilder(query);	
			
			boolean first = true;
			for(String column : parameters.keySet()){
				if (first){
					first = false;
					tempQuery.append(" ( " + column);
				}
				else{
					tempQuery.append( ", " + column);
				}
			}
			tempQuery.append( " ) ");
			
			first = true;
			for(String column : parameters.keySet()){
				if (first){
					first = false;
					tempQuery.append(" VALUES ( " + ":"+column);
				}
				else{
					tempQuery.append(", "  +  ":" + column);
				}
			}
			tempQuery.append( " ) ");
		
			query = tempQuery.toString();
		}
				
		buildStatement(QueryType.INSERT);

		pstmt.executeUpdate();

		ResultSet rs = pstmt.getGeneratedKeys();
		if (rs.next()){
			pk  = rs.getInt(1);
		}
		buildAddQuery = false;

		return pk;
	}	

	/**
	 * User to insert record in batch
	 * @throws SQLException
	 */
	public void addToBatch() throws SQLException{

		//if the 1st occurence of the batch, then create the query.. 
		if (addBatchfirstValue){
			if (!StringUtl.containsIgnoreCase(query, "insert")){
				close();
				throw new SQLException("Error in QUERY. The ADD Method is only used when inserting a new record on a table.");
			}		

			if (parameters.size() > 0 && buildAddQuery){

				StringBuilder tempQuery = new StringBuilder(query);	

				boolean first = true;
				for(String column : parameters.keySet()){
					if (first){
						first = false;
						tempQuery.append(" ( " + column);
					}
					else{
						tempQuery.append( ", " + column);
					}
				}
				tempQuery.append( " ) ");

				first = true;
				for(String column : parameters.keySet()){
					if (first){
						first = false;
						tempQuery.append(" VALUES ( " + ":"+column);
					}
					else{
						tempQuery.append(", "  +  ":" + column);
					}
				}
				tempQuery.append( " ) ");

				query = tempQuery.toString();
			}

			buildStatement(QueryType.SELECT);
			
			addBatchfirstValue = false;
		}
		else{
			addParameters();
		}

		pstmt.addBatch();

		//reset values
		parameters =  new LinkedHashMap<String, Object>();
	}
	
	public void executeBatchQuery() throws SQLException{
		if (pstmt != null){
			pstmt.executeBatch();
		}else
		{
			close();
			throw new SQLException("Prepared statement null");
		}
	}
	
	
	
	public String returnQuery(){
		return this.query;
	}
	/**
	 * Add the WHERE clause to the update query
	 * Example: 	DBConnection db = new DBConnection();
	 * 				db.buildUpdateQuery(User.TBL_NAME)
	 *					.setParameter(User.EMAIL, "generic email")
	 *					.setParameter(User.NBR_TRIES, 22)			
	 *					.addUpdWhereClause("WHERE "+ User.ID + " = :userId OR " + User.ID + " = :userId2 ", 2, 44)
	 *					.update();
	 * 
	 * @param where	- The where clause to add to the query.	 
	 * @param values - Values , if needed, to be added to the parameters of the WHERE query.
	 * @return
	 * @throws SQLException
	 */
	public DBConnection addUpdWhereClause(String where,  Object ... values) throws SQLException{
		
		if (where == null || where.length() == 0){
			close();
			throw new SQLException("Please inlude where clause");
		}
		if(!StringUtl.containsIgnoreCase(where, "where")){
			close();
			throw new SQLException("Please inlude WHERE in query. Query: " + where);
		}		
		
		if (updateParam == null){
			updateParam = new LinkedHashMap<String, Object>();
		}
		
		whereStatment = " " + where;
		
		//get the parameters
		int idx = 0;
		for(String st : where.split(" ")){
		    if(st.startsWith(":") && st.length() > 1){
		    	String param = st.substring(1,st.length());
		    	if (updateParam.containsKey(param) || parameters.containsKey(param)){
		    		close();
		    		throw new SQLException("More than 1 parameter of name: " + param + ". The parameter must be unique");
		    	}
		    	
		    	try{
		    		updateParam.put(param, values[idx]);
			    	idx++;
		    	}catch (IndexOutOfBoundsException ix){
		    		close();
		    		throw new SQLException("There are more parameters than values, please add equal values to the parameters");
		    	}
		    }
		}		
		return this;		
	}
	/**
	 * Sort the parameters to follow the order that they appear in the qeury string.
	 * @param order
	 */
	private void sortParameters(Map<String, Integer> order){

		Map<String, Object> orderedParam = new LinkedHashMap<String, Object>();

		List<Map.Entry<String, Integer>> entries =
				new ArrayList<Map.Entry<String, Integer>>(order.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b){
				return a.getValue().compareTo(b.getValue());
			}
		});

		for(Map.Entry<String, Integer> idx : entries){
			orderedParam.put(idx.getKey(), parameters.get(idx.getKey()));
		}

		parameters.clear();
		parameters.putAll(orderedParam);		
	}

	public static void main(String args[]) throws SQLException{
//		DBConnection db = new DBConnection("theUrl","theUserName","thePass",DbClass.H2);

//		//--> UPDATE
//		System.out.println("\n -----------  UPDATE  --------------");
//		String updateQuery = "UPDATE " + User.TBL_NAME + " SET " + User.LAST_NAME + " = :lstName WHERE id  = :userId" ;
//
//		db.createQuery(updateQuery)
//		  .setParameter("userId", 2)
//		  .setParameter("lstName", "MyFriendBob")
//		  .update();
//	
////		add.append("INSERT INTO " + TBL_NAME);
////		add.append( " ( " + USER_NAME + "," + PASSWORD + "," + FIRST_NAME + "," + LAST_NAME + "," + EMAIL + "," + LAST_LOGIN + "," + NBR_TRIES + "," + ACCESS +" ) " );
////		add.append(" VALUES(?,?,?,?,?,?,?,?)");
//				
//		//--> ADDD  EXAMPLE 1
//		System.out.println("\n -----------  Add example 1  --------------");
//		String addQuery = "INSERT INTO " + User.TBL_NAME + " ( " + User.USER_NAME + " , " + User.PASSWORD + " , " + User.ACCESS + " ) VALUES (:userName, :password, :access)"; //example 1
//		
//		int pk = db.createQuery(addQuery)
//				.setParameter("userName", "bob")
//				.setParameter("password", "1234561")
//				.setParameter("access", AccessEnum.ADMIN.name())
//				.add();		
//		System.out.println("PK: " + pk);
//		
//		//--> ADDD  EXAMPLE 2	
//		System.out.println("\n -----------  ADD example 2  --------------");
//		int pkKey = db.buildAddQuery(User.TBL_NAME)		
//		  .setParameter(User.USER_NAME, "bobo")
//		  .setParameter(User.PASSWORD, "12121")
//		  .setParameter(User.ACCESS, AccessEnum.REGULAR.name())
//		  .add();
//		
//		//--> SELECT Multiple entries
//		//		String query = "SELECT id, " + User.USER_NAME + " FROM " + User.TBL_NAME + " where id > :bob2";
//		System.out.println("\n -----------  Select  --------------");
//		String query = "SELECT * FROM " + User.TBL_NAME + " where id > :bob2";
//
//		ResultSet rs = db.createQuery(query)
//				.setParameter("bob2", 0)
//				.getSelectResultSet();
//
//		if (rs != null){
//
//			while (rs.next()){
//				User user = new User(rs);
//				System.out.println("User : " + user);
//			}
//		}
//		//--> DELETE
//		System.out.println("\n -----------  Delete  --------------");
//		String delete = "DELETE FROM " + User.TBL_NAME + " where id = :userId";
//		db.createQuery(delete)
//		.setParameter("userId", pkKey).delete();
//		
//		//--> SELECT Multiple entries	
//		System.out.println("\n -----------  Select  --------------");
//		String query2 = "SELECT * FROM " + User.TBL_NAME + " where id > :bob2";
//
//		ResultSet rs2 = db.createQuery(query2)
//				.setParameter("bob2", 0)
//				.getSelectResultSet();
//
//		if (rs2 != null){
//
//			while (rs2.next()){
//				User user = new User(rs2);
//				System.out.println("User : " + user);
//			}
//		}
//			
//		//New Update query
//		System.out.println("\n -----------  NEW Update  --------------");
//		db.buildUpdateQuery(User.TBL_NAME)
//			.setParameter(User.EMAIL, "generic " + new Date().toString())
//			.setParameter(User.NBR_TRIES, 22)			
//			.addUpdWhereClause("WHERE "+ User.ID + " = :userId OR " + User.ID + " = :userId2 ", 2, 44)
//			.update();
//		
//		
//		System.out.println("\n -----------  Select  --------------");
//		String query3 = "SELECT * FROM " + User.TBL_NAME + " where id > :bob2";
//
//		ResultSet rs3 = db.createQuery(query3)
//				.setParameter("bob2", 0)
//				.getSelectResultSet();
//
//		if (rs3 != null){
//
//			while (rs3.next()){
//				User user = new User(rs3);
//				System.out.println("User : " + user);
//			}
//		}
		
		/////////Execute batch
//		db.buildAddQuery(User.TBL_NAME);
//		 
//		for(int i = 0; i < 3 ; i ++){
//			db.setParameter(User.USER_NAME, "bobo"+i)
//			.setParameter(User.PASSWORD, "12121" + i)
//			.setParameter(User.ACCESS, AccessEnum.REGULAR.name())
//			.addBatch();
//		}
//		
//		db.executeBatchQuery();
		 
		//
		//create table
		//
		
//		Map<String, String> columns = new HashMap<String, String>();
//		columns.put("id", "INT PRIMARY KEY auto_increment");
//		columns.put("running", "BOOLEAN");
//		columns.put("stop", "TINYINT");		
//		
//		db.createTable("testTbl", columns);
		
		
		
//		db.close();

	}
	public Connection getConnection(){
		return this.conn;		
	}
	
	/**
	 * The type of object is being added into the preference.
	 */
	public enum QueryType {
		SELECT, UPDATE, DELETE, INSERT;
	}
}
