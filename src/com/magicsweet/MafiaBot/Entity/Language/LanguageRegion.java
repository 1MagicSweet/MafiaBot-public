package com.magicsweet.MafiaBot.Entity.Language;

public enum LanguageRegion {
RUSSIA,
UNITED_STATES,
UKRAINE,
KIEVAN_RUSSIA,
KAZAKHSTAN;
	private String languageName;
	private String englishLanguageName;
	private String languageCode;
	static {
		RUSSIA.languageName = "Россия";
		RUSSIA.englishLanguageName = "Russia";
		RUSSIA.languageCode = "ru";
		
		UNITED_STATES.languageName = "United States of America";
		UNITED_STATES.englishLanguageName = "United States of America";
		UNITED_STATES.languageCode = "us";
		
		UKRAINE.languageName = "Україна";
		UKRAINE.englishLanguageName = "Ukraine";
		UKRAINE.languageCode = "ua";
		
		KIEVAN_RUSSIA.languageName = "Кіевская Русь";
		KIEVAN_RUSSIA.englishLanguageName = "Kievan Russia";
		KIEVAN_RUSSIA.languageCode = "kru";
		
		KAZAKHSTAN.languageName = "Қазақстан";
		KAZAKHSTAN.englishLanguageName = "Kazakhstan";
		KAZAKHSTAN.languageCode = "kz";
	}
	public String getRegionName() {
		return languageName;
	}
	public String getEnglishRegionName() {
		return englishLanguageName;
	}
	public String getCode() {
		return languageCode;
	}
	public LanguageRegion getByCode(String code) {

		return null;
	}
}
