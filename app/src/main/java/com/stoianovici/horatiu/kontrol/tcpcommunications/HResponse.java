package com.stoianovici.horatiu.kontrol.tcpcommunications;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class HResponse {
	private TCPStatusCodes statusCode;
	private String responseContent;
	
	public HResponse(String rawResponse){
		JSONParser parser = new JSONParser();
		responseContent = "";
		
		JSONObject parsed;
		try {
			parsed = (JSONObject)(parser.parse(rawResponse));
			String statusCode = parsed.get("StatusCode").toString();
			this.statusCode = TCPStatusCodes.getEnumValue(Integer.parseInt(statusCode));
			this.responseContent = parsed.get("Content").toString();
		} catch (Exception e) {
			statusCode = TCPStatusCodes.CouldNotParse;
			return;
		}
	}
	
	public String getContent(){
		return responseContent;
	}
	
	public TCPStatusCodes getStatusCode(){
		return statusCode;
	}
}
