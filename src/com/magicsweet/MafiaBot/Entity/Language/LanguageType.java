package com.magicsweet.MafiaBot.Entity.Language;

public enum LanguageType {
RUSSIAN,
ENGLISH,
UKRAINIAN,
ANCIENT_RUSSIAN,
KAZAKH;
	private String languageName;
	private String englishLanguageName;
	private String languageCode;
	static {
		RUSSIAN.languageName = "Русский";
		RUSSIAN.englishLanguageName = "Russian";
		RUSSIAN.languageCode = "ru";
		
		ENGLISH.languageName = "English";
		ENGLISH.englishLanguageName = "English";
		ENGLISH.languageCode = "en";
		
		UKRAINIAN.languageName = "Український";
		UKRAINIAN.englishLanguageName = "Ukrainian";
		UKRAINIAN.languageCode = "ua";
		
		ANCIENT_RUSSIAN.languageName = "Русскiй";
		ANCIENT_RUSSIAN.englishLanguageName = "Ancient Russian";
		ANCIENT_RUSSIAN.languageCode = "aru";
		
		KAZAKH.languageName = "Қазақ";
		KAZAKH.englishLanguageName = "Kazakh";
		KAZAKH.languageCode = "kk";
	}
	public String getLanguageName() {
		return languageName;
	}
	public String getEnglishLanguageName() {
		return englishLanguageName;
	}
	public String getCode() {
		return languageCode;
	}
	public LanguageType getByCode(String code) {

		return null;
	}
}
