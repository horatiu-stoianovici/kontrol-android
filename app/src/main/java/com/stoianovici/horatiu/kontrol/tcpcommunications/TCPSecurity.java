package com.stoianovici.horatiu.kontrol.tcpcommunications;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;

public class TCPSecurity {
	private static ServerInfo connectedServer;
	
	/**
	 * Attempts to connect to server
	 */
	public static boolean AttemptLogin(ServerInfo server, Context context) throws UnknownHostException, IOException{
		//create a request for the authorize command, sending the mac address as a parameter
		HRequest request = new HRequest("authorize", new ArrayList<String>(Arrays.asList(ClientInfo.getMacAddress(context))), server.getAddressString());
		HResponse response = request.sendPriority();
		
		if(response.getStatusCode() == TCPStatusCodes.Ok){
			connectedServer = server;
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * Gets the server that is currently connected
	 * @return
	 */
	public static ServerInfo getConnectedServer() {
		return connectedServer;
	}
}
