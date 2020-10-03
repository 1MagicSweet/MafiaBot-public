package com.magicsweet.MafiaBot.Entity;

public class RoleData {
	StringBuffer data = new StringBuffer();
	public RoleData(String data) {
		this.data.append(data);
	}
	public void addData(String key, String value) {
		data.append("," + key + "=" + value);
	}
	public void removeData(String key) {
		data.delete(data.indexOf(key + "="), data.indexOf(",", data.indexOf(key + "=")));
	}
	public String getData(String key) {
		return data.substring(data.indexOf("=", data.indexOf(key + "=")) + 1, data.indexOf(",", data.indexOf(key + "=") - 1));
	}
	public String getData() {
		return data.toString();
	}
}
