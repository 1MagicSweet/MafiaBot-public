package com.magicsweet.MafiaBot.Entity;

import java.util.ArrayList;
import java.util.Collection;

import com.magicsweet.MafiaBot.Entity.Language.Language;
import com.magicsweet.MafiaBot.Entity.Language.LanguageRegion;
import com.magicsweet.MafiaBot.Entity.Language.LanguageType;

public class GameSettings {
	Language lang;
	Collection<Role> roles = new ArrayList<Role>();
	int playerCount;
	String includedRoles;
	StringBuffer overridedRoles = new StringBuffer();
	public GameSettings(String includedRoles, int playerCount) {
		this.playerCount = playerCount;
		this.includedRoles = includedRoles;
		addDefaults();
	}
	public GameSettings(String includedRoles, int playerCount, Language lang) {
		this.playerCount = playerCount;
		this.includedRoles = includedRoles;
		this.lang = lang;
		addDefaults();
	}
	public GameSettings(Language lang) {
		this.lang = lang;
		addDefaults();
	}
	public GameSettings getSettings() {
		return this;
	}
	public Collection<Role> getRoles() {
		return roles;
	}
	public void setRoles(String includedRoles, int playerCount) {
		this.includedRoles = includedRoles;
		this.playerCount = playerCount;
	}
	public Language getLanguage() {
		return lang;
	}
	public void setLanguage(Language lang) {
		this.lang = lang;
	}
	
	
	@Deprecated
	public void setDefaults() {
		lang = new Language(LanguageType.RUSSIAN, LanguageRegion.RUSSIA);
	}
	
	public void addDefaults() {
		if (lang == null) lang = new Language(LanguageType.RUSSIAN, LanguageRegion.RUSSIA); 
	}
	
	public void setPlayerCount(int count) {
		this.playerCount = count;
	}
	
	public void addRoleOvverride(String role) {
		overridedRoles.append(role + ",");
	}
	
	public void removeRoleOverride(String role) {
		try {
		overridedRoles = overridedRoles.delete(overridedRoles.indexOf(role), overridedRoles.indexOf(",", overridedRoles.indexOf(role)) + 1);
		} catch (StringIndexOutOfBoundsException e) {
			
		}
	}
	
	public String getRoleOverrides() {
		if (overridedRoles.toString().equals("")) return null; 
		return overridedRoles.toString();
	}
	
	public GameSettings build() {
		roles.clear();
		
		StringBuffer strb = new StringBuffer(includedRoles.replace(", ", ","));
		
		if (overridedRoles != null) {
			String over = overridedRoles.toString();
			
			for (String s : over.split(",")) {
				String temp = strb.toString();
				String[] values = s.split(":");
				System.out.println(s);
				if (values.length == 2) {
					if (Boolean.parseBoolean(values[1])) {
						if (!temp.contains(values[0])) {
							
							strb.append("," + values[0]);
							
						}
					} else {
						if (temp.contains(values[0])) {
							
							temp = temp.replace(values[0] + ",", "");
							strb = new StringBuffer(temp);
						
						}
					}
				}
			}
		}
		
		String[] roleList = strb.toString().replace(", ", ",").split(",");
		
		for (int t = 0; t < playerCount; t++) {
			try {
				roles.add(new Role(RoleType.valueOf(roleList[t].toUpperCase()), lang));
			} catch (ArrayIndexOutOfBoundsException e) {	
				roles.add(new Role(RoleType.INNOCENT, lang));
			}
		}
		
		System.out.println(strb.toString());
		System.out.println(roles.size());
		addDefaults();
		return this;
	}
}
