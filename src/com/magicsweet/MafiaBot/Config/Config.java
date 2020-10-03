package com.magicsweet.MafiaBot.Config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;



public class Config {
	String path;
	public Config (String path) {
		this.path = path;
	}
	public void setConfigPath(String path) {
		this.path = path;
		  
	}
	public String getValue(String key) throws IOException {
		List<String> strings =  Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
		String output = null;
		for (int a = 0; a < strings.size(); a++) {
			if (strings.get(a).startsWith(key + ": ")) {
				output = strings.get(a).replace(key + ": ", "");
			} else if (strings.get(a).startsWith(key + ":")) {
				output = strings.get(a).replace(key + ":", "");
			}
		}
		return output;
	}
	public void setValue(String key, String value) throws IOException {
		List<String> strings =  Files.readAllLines(Paths.get(path));
		String line = null;
		for (int a = 0; a < strings.size(); a++) {
			if (strings.get(a).startsWith(key + ": ")) {
			line = key + ": " + value;
			strings.set(a, line);
			} else if (strings.get(a).startsWith(key + ":")) {
			line = key + ": " + value;
			strings.set(a, line);
			}
			Files.write(Paths.get(path), strings, StandardCharsets.UTF_8);
		}
	}
}

