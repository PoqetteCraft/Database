package com.github.pocketkid2.database;

import org.bukkit.ChatColor;

public class Messages {

	public interface Command {
		String CONNECT_SUCCESS = "Connection successful!";
		String CONNECT_FAILURE = "Connection failed!";
		String RECONNECT_SUCCESS = "Reconnection successful!";
		String RECONNECT_FAILURE = "Reconnection failed!";
		String DISCONNECT_SUCCESS = "Disconnected!";
		String DATABASE_ALREADY_ONLINE = "The database is already online!";
		String DATABASE_ALREADY_OFFLINE = "The database is already offline!";
		String TOO_MANY_ARGS = ChatColor.RED + "Too many arguments!";
		String UNKNOWN_ARG = ChatColor.RED + "Unknown argument '%s'";
	}

	public interface Console {
		String CONNECT_SUCCESS = "Connected to the database!";
		String CONNECT_FAILURE = "Connection failure!";
		String DISCONNECT_SUCCESS = "Disconnected from the database!";
		String DISCONNECT_FAILURE = "Disconnection failure!";
		String PREPARED_STATEMENT_FAILURE = "Error creating prepared statement, please check connection!";
		String STATEMENT_FAILURE = "Error creating statement, please check connection!";
		String TIMEOUT_INVALID = "Timeout value is less than 0!";
	}
}
