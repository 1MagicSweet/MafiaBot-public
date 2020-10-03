package com.magicsweet.MafiaBot.Entity.Language;

import java.io.IOException;

import com.magicsweet.MafiaBot.Config.Config;

public class Language {
	LanguageType type;
	LanguageRegion region;
	Config en = new Config("lang/en_us.yml");
	Config ru = new Config("lang/ru_ru.yml");
	Config lang;
	public Language(LanguageType type, LanguageRegion region)	{
		this.type = type;
		this.region = region;
		
		if (type != LanguageType.ENGLISH && region != LanguageRegion.UNITED_STATES) {
			lang = new Config("lang/" + getCode() + ".yml");
		} else {
			lang = new Config("lang/en_us.yml");
		}
	}
	
	public String getString(String key) {
		try {
			if (lang.getValue(key) == null) {
				if (ru.getValue(key) != null) return ru.getValue(key).replace("[new_line]", "\n");
				return en.getValue(key).replace("[new_line]", "\n");
			}
			return lang.getValue(key).replace("[new_line]", "\n");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public LanguageType getType() {
		return type;
	}
	public LanguageRegion getRegion() {
		return region;
	}
	
	public String getCode() {
		return type.getCode() + "_" + region.getCode();
	}
}
