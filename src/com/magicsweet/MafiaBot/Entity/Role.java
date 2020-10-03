package com.magicsweet.MafiaBot.Entity;

import com.magicsweet.MafiaBot.Entity.Language.Language;

public class Role {
	RoleType type;
	RoleData data;
	Language lang;
	RoleColor color;
	public Role(RoleType type, Language lang) {
	this.type = type;
	this.lang = lang;
	
	this.color = generateColor();
	}
	public Role(RoleType type, RoleData data, Language lang) {
	this.type = type;
	this.data = data;
	this.lang = lang;
	
	this.color = generateColor();
	}
	void setRoleData(RoleData data, Language lang) {
		this.data = data;
		this.lang = lang;
		
		this.color = generateColor();
	}
	RoleData getRoleData() {
		return data;
	}
	public RoleType getType() {
		return type;
	}
	
	RoleColor generateColor() {
		switch (type) {
		case INNOCENT:
			return RoleColor.RED;
		case MAFIA:
			return RoleColor.BLACK;
		case MAFIA_DON:
			return RoleColor.BLACK;
		case DETECTIVE:
			return RoleColor.RED;
		case DOCTOR:
			return RoleColor.RED;
		case SINGLETON:
			return RoleColor.NEUTRAL;
		case JOURNALIST:
			return RoleColor.RED;
		case THIEF:
			return RoleColor.BLACK;
		case PROSPECTOR:
			return RoleColor.RED;
		case YAKUZA:
			return RoleColor.BLACK;
		case SHERIFF:
			return RoleColor.RED;
		case GAMBLER:
			return RoleColor.BLACK;
		default:
			return null;
		}
	}
	
	public String getName() {
		switch (type) {
		case INNOCENT:
			return lang.getString("innocent-role-name");
		case MAFIA:
			return lang.getString("mafia-role-name");
		case MAFIA_DON:
			return lang.getString("mafia-don-role-name");
		case DETECTIVE:
			return lang.getString("detective-role-name");
		case DOCTOR:
			return lang.getString("doctor-role-name");
		case SINGLETON:
			return lang.getString("singleton-role-name");
		case JOURNALIST:
			return lang.getString("journalist-role-name");
		case THIEF:
			return lang.getString("thief-role-name");
		case PROSPECTOR:
			return lang.getString("prospector-role-name");
		case YAKUZA:
			return lang.getString("yakuza-role-name");
		case SHERIFF:
			return lang.getString("sheriff-role-name");
		case GAMBLER:
			return lang.getString("liar-role-name");
		default:
			return lang.getString("unknown-role-name");
		
		}
	}
}
