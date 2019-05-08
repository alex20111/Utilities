package home.miniHttp;

import java.util.ArrayList;
import java.util.List;

public class Table {

	private int nbrOfColumn = 0;
	private List<String> tableHeader = new ArrayList<String>();
	
	private List<String> tableBody = new ArrayList<String>();
	
	private StringBuilder tableClass = new StringBuilder();
	private StringBuilder tableStyle = new StringBuilder();
	private StringBuilder theaderClass = new StringBuilder();
	
	private StringBuilder tableContent = new StringBuilder();
	
	
	public Table(int numberOfColumn){
		this.nbrOfColumn = numberOfColumn;
		tableContent.append("<table");
	}
	
	public void addHeader(String headerName) throws Exception{
		if (nbrOfColumn == 0 )
		{
			throw new Exception("Please enter a number of columns");
		}
		if (tableHeader.size() == 8 )
		{
			throw new Exception("Maximum number of header. Increase column number to add more headers");
		}
		
		tableHeader.add(headerName);
	}
	
	public void addTableClass(String className){
		if (tableClass.length() > 0){
			tableClass.append(" " + className);
		}else{
			tableClass.append(className);	
		}
	}
	public void addTheaderClass(String className){
		if (theaderClass.length() > 0){
			theaderClass.append(" " + className);
		}else{
			theaderClass.append(className);	
		}
	}
	public void addTableStyle(String styleName){
		if (tableStyle.length() > 0){
			tableStyle.append(";" + styleName);
		}else{
			tableStyle.append(styleName);	
		}
	}
	public void addTableBody(String body){
		tableBody.add(body);
	}	
	
	public String build(){
		if (tableClass.length() > 0){
			tableContent.append(" class=\"" + tableClass + "\" ");			
		}
		if (tableStyle.length() > 0){
			tableContent.append(" style=\"" + tableStyle + "\" ");
		}
		tableContent.append(">");
		
		if (tableHeader.size() > 0){
			tableContent.append("<thead ");
			if (theaderClass.length() > 0){
				tableContent.append("class=\""+theaderClass + "\"");
			}
			tableContent.append("><tr>");
			
			for(String hdr : tableHeader){
				tableContent.append("<th>");
				tableContent.append(hdr);
				tableContent.append("</th>");
			}
			tableContent.append("</tr></thead>");
		}
		if (tableBody.size() > 0){
			tableContent.append("<tbody>");		
			
			int idx = 0;
			boolean keepRunning = true;
			while(keepRunning){
				tableContent.append("<tr>");
				for(int i = 0; i < nbrOfColumn ; i ++){					
					tableContent.append("<td>");
					tableContent.append(tableBody.get(idx));
					tableContent.append("</td>");
					if (idx == tableBody.size() - 1){
						keepRunning = false;
						break;
					}
					idx++;
				}
				tableContent.append("</tr>");
			}			

			tableContent.append("</tbody>");
		}		
		tableContent.append("</table>");
		return tableContent.toString();
	}
}