package com.stoianovici.horatiu.kontrol.tcpcommunications;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class ServerInfo {
	private String ipAddress, name;
	private NetworkDevice device;
	private boolean isConnecting = false;
	private String connectedMessage = null;
	private String id;
	
	public ServerInfo(String rawMesssage) throws Exception{
		JSONParser parser = new JSONParser();
		JSONObject parsed = (JSONObject)parser.parse(rawMesssage);
		
		this.ipAddress = parsed.get("IPAddress").toString();
		this.name = parsed.get("HostName").toString();
		this.device = NetworkDevice.getEnumValue(Integer.parseInt(parsed.get("Device").toString()));
		this.id = parsed.get("HostId").toString();
	}

	public ServerInfo(String name, NetworkDevice device, String address){
		this.name = name;
		this.ipAddress = address;
		this.device = device;
	}
	
	public String getAddressString() {
		return ipAddress;
	}

	public void setAddressString(String ipAddressString){
		ipAddress = ipAddressString;
	}

	public String getName() {
		return name;
	}

	public NetworkDevice getDevice(){
		return device;
	}

	public void setIsConnecting(boolean b) {
		this.isConnecting = b;
	}
	
	public boolean isConnecting(){
		return this.isConnecting;
	}

	public void setConnectedMessage(String string) {
		this.connectedMessage = string;
	}
	
	public String getConnectedMessage(){
		return this.connectedMessage;
	}
	
	public String getId(){
		return this.id;
	}
}
