package com.stoianovici.horatiu.kontrol.tcpcommunications;

public enum TCPStatusCodes {
	Ok(200),
	NotAuthorized(303),
	WrongFormat(405),
	NotFound(404),
	CouldNotParse(101);
	
	int code;
	
	private TCPStatusCodes(int code){
		this.code = code;
	}
	
	public int getCode(){
		return this.code;
	}
	
	public static TCPStatusCodes getEnumValue(int intValue){
		for (TCPStatusCodes iterable_element : TCPStatusCodes.values()) {
			if(iterable_element.code == intValue){
				return iterable_element;
			}
		}
		return null;
	}
}
