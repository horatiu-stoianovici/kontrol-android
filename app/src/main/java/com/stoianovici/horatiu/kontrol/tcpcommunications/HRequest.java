package com.stoianovici.horatiu.kontrol.tcpcommunications;

import com.stoianovici.horatiu.kontrol.utils.BluetoothService;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.simple.JSONObject;

public class HRequest {
	private ArrayList<String> parameters;
	private String commandName;
	private String ipAddress = null;
	
	public HRequest(){
		this("", new ArrayList<String>());
	}
	
	public HRequest(String commandName){
		this(commandName, new ArrayList<String>());
	}
	
	public HRequest(String commandName, Collection<String> parameters){
		this.commandName = commandName;
		this.parameters = new ArrayList<String>(parameters);
	}
	
	public HRequest(String commandName, Collection<String> parameters, String ipAddress){
		this(commandName, parameters);
		this.ipAddress = ipAddress;
	}
	/**
	 * <p>Adding a single parameter to the request</p>
	 * @param parameter
	 */
	public void addParameter(String parameter){
		parameters.add(parameter);
	}
	
	/**
	 * <p>Adding multiple parameters at once to the request</p>
	 * @param parameters
	 */
	public void addParameters(Collection<String> parameters){
		this.parameters.addAll(parameters);
	}
	
	/**
	 * Returns the string representation of the request (JSON format)
	 */
	@SuppressWarnings("unchecked")
	public String toString(){
		JSONObject obj = new JSONObject();
		obj.put("CommandName", commandName);
		obj.put("MACAddress", ClientInfo.getMacAddress(null));
		obj.put("Parameters", new JSONArray(parameters));
		
		return obj.toJSONString();
	}

	private HResponse sendBluetooth(){

		BluetoothService.getInstance(null, null).write(this.toString().getBytes());
		return null;
	}

	/**
	 * Sends a request via TCP and gets the result
	 */
	public HResponse sendPriority() throws UnknownHostException, IOException{
		if(BluetoothService.IS_ACTIVE){
			return sendBluetooth();
		}
		Socket clientSocket = new Socket(); 
		clientSocket.connect(new InetSocketAddress(ipAddress != null ? ipAddress : TCPSecurity.getConnectedServer().getAddressString(), ClientInfo.PortTcp), 10000);
		
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());  
		BufferedReader inFromServer =  new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		outToServer.writeBytes(this.toString() + "\n");

		String responseText = inFromServer.readLine();
		HResponse response = new HResponse(responseText);
		clientSocket.close();
		
		return response;
	}
	
	/**
	 * Sends a request via UDP and does not return any result
	 */
	public HResponse sendNonPriority() throws IOException {
		if(BluetoothService.IS_ACTIVE){
			return sendBluetooth();
		}
		DatagramSocket socket = new DatagramSocket();
		socket.connect(new InetSocketAddress(TCPSecurity.getConnectedServer().getAddressString(), 4672));
		byte[] buf = this.toString().getBytes();
		DatagramPacket packet = new DatagramPacket(buf, buf.length);;

		/* Send out the packet */
		socket.send(packet);
		
		return null;
	}
}
