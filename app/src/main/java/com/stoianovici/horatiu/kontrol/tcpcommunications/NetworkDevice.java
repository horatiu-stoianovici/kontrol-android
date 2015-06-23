package com.stoianovici.horatiu.kontrol.tcpcommunications;

public enum NetworkDevice {
	WiFi(1),
	Bluetooth(2);
	
	int code;
	
	private NetworkDevice(int code){
		this.code = code;
	}
	
	public int getCode(){
		return this.code;
	}
	
	public static NetworkDevice getEnumValue(int intValue){
		for (NetworkDevice iterable_element : NetworkDevice.values()) {
			if(iterable_element.code == intValue){
				return iterable_element;
			}
		}
		return null;
	}
}
