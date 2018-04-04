package com.github.pocketkid2.database;

import org.bukkit.configuration.file.FileConfiguration;

public class Settings {

	// Server connection values
	private String hostname;
	private String port;
	private String username;
	private String password;
	private String database;

	// Validation values
	private int timeout;
	private int repeat;

	public Settings(FileConfiguration config) {
		// Populate connection values
		hostname = config.getString("connection.host");
		port = config.getString("connection.port");
		username = config.getString("connection.user");
		password = config.getString("connection.pass");
		database = config.getString("connection.db");

		// Populate validation values
		timeout = config.getInt("validation-timeout");
		repeat = config.getInt("validation-repeat");
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

	public int getRepeat() {
		return repeat;
	}

	public int getTimeout() {
		return timeout;
	}

}
