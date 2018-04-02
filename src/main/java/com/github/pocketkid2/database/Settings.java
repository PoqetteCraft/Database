package com.github.pocketkid2.database;

import org.bukkit.configuration.file.FileConfiguration;

public class Settings {

	private String hostname;
	private String port;
	private String username;
	private String password;
	private String database;

	public Settings(FileConfiguration config) {
		hostname = config.getString("connection.host");
		port = config.getString("connection.port");
		username = config.getString("connection.user");
		password = config.getString("connection.pass");
		database = config.getString("connection.db");
	}

	public String getURL() {
		return String.format("jdbc:mysql://%s:%s/%s?useSSL=false", hostname, port, database);
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

}
