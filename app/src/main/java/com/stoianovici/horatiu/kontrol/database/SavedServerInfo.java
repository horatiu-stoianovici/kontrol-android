package com.stoianovici.horatiu.kontrol.database;

public class SavedServerInfo {
	private String key, password;
	private long id;
	
	public String getKey() {
		return key;
	}

	public String getPassword() {
		return password;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getId() {
		return id;
	}

}
