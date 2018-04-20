package home.thingSpeak;


import java.util.HashMap;
import java.util.Map;

import home.inet.Connect;

public class ThingSpeak {

	private Map<String, String> fieldValues = new HashMap<String, String>();
	private String thingUrl = "https://api.thingspeak.com";
	private String apiKey = "";
	
	public ThingSpeak(String apiKey) {
		this.apiKey = apiKey;
		
	}
	
	public static void main(String[] args) throws Exception {
		//I3XZBHBDHJBQTOO7
		ThingSpeak t = new ThingSpeak("I3XZBHBDHJBQTOO7");
		t.addField(1, "200");
		String res = t.write();
		
		System.out.println("res " + res);

	}
	public ThingSpeak addField(int fieldId, String fieldValue) {
		fieldValues.put("field"+fieldId, fieldValue);
		return this;
	}
	public String write() throws Exception {
		if (!fieldValues.isEmpty()) {
			thingUrl += "/update?api_key="+apiKey;
			for(Map.Entry<String, String> field : fieldValues.entrySet()) {
				thingUrl += "&"+field.getKey()+"="+field.getValue();
			
			}
			
			Connect con = new Connect(thingUrl);
			return con.connectToUrlUsingGET().getResultAsStr();
		}
		else
		{
			throw new Exception("Empty fields. It needs at least 1 field to be able to write");
		}
	}

}
