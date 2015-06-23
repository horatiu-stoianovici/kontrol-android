package com.stoianovici.horatiu.kontrol.tcpcommunications;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Gets info about the phone
 * @author Horatiu
 */
public class ClientInfo {
	private static volatile String macAddr = null;
	public static final int PortTcp = 8001;
	public static final int PortUdp = 4672;

	/**
	 * The ip address for the multicast messages for server detection
	 */
	public static final String MulticastIp = "234.6.7.2";

	/**
	 * Get the mac address for the phone
	 */
	public static String getMacAddress(Context ctx){
		if(macAddr == null){
			if(ctx == null){
				return "bluetooth-3241";
			}
			WifiManager mainWifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
			WifiInfo currentWifi = mainWifi.getConnectionInfo();
			macAddr = currentWifi.getMacAddress();
		}

		return macAddr;
	}
}
