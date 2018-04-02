package com.github.pocketkid2.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * The main API class, used as a singleton. All API calls should be made through
 * this.
 *
 * @author Adam
 *
 */
public class Database {

	// The instance of the main class, used for determining current status
	private static DatabasePlugin plugin;
	// A list of all registered plugins
	private static Set<DatabasePlugin> registeredPlugins;
	// The database connection
	private static Connection connection;
	// The values for connection
	private static Settings settings;

	/*
	 *
	 * INTERNAL API Called by the first instance of DatabasePlugin, used to
	 * initialize this Database instance with config values and such
	 *
	 * @param plugin The instance of the base plugin class
	 * 
	 * @param settings The container class for the connection values
	 */
	protected static void initialize(DatabasePlugin plugin, Settings settings) {
		Database.plugin = plugin;
		Database.settings = settings;
		Database.registeredPlugins = new HashSet<DatabasePlugin>();
		Database.connect();
	}

	/**
	 * Any plugin that extends DatabasePlugin needs to register with this method.
	 * This will also give the initial condition of the plugin.
	 *
	 * @param pl
	 *            The plugin instance to register
	 */
	public static void register(DatabasePlugin pl) {
		registeredPlugins.add(pl);
		pl.setOnline(Database.plugin.isOnline());
	}

	/*
	 * Internal API for getting registered plugins
	 *
	 * @return The list of registered plugins
	 */
	protected static Set<DatabasePlugin> getRegisteredPlugins() {
		return registeredPlugins;
	}

	/**
	 * Attempts to connect to the database, given that the database is in a
	 * disabled/disconnected state.
	 *
	 * If successful, notifies all registered plugins.
	 */
	protected static void connect() {
		// Must be in a disabled state
		if (!plugin.isOnline()) {
			// Grab the URL and user/pass
			try {
				connection = DriverManager.getConnection(settings.getURL(), settings.getUsername(), settings.getPassword());
				plugin.setOnline(true);
				plugin.getLogger().info("Connected to the database!");
			} catch (SQLException e) {
				plugin.setOnline(false);
				plugin.getLogger().severe("Could not connect to the database!");
			}
			// Now update all registered plugins
			for (DatabasePlugin pl : registeredPlugins) {
				pl.setOnline(plugin.isOnline());
			}
		}
	}

	/**
	 * Disconnects from the database, assuming that the connection is live.
	 *
	 * Notifies all registered plugins.
	 */
	protected static void disconnect() {
		// Must be in an enabled state
		if (plugin.isOnline()) {
			try {
				connection.close();
				plugin.getLogger().info("Disconnected successfully!");
			} catch (SQLException e) {
				plugin.getLogger().severe("Disconnection failure!");
			}
			// Update all plugins
			plugin.setOnline(false);
			for (DatabasePlugin pl : registeredPlugins) {
				pl.setOnline(plugin.isOnline());
			}
		}
	}

}
