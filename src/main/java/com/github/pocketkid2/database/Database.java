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

	/**
	 * Called by the first instance of DatabasePlugin, used to initialize this
	 * Database instance with config values and such
	 *
	 * @param plugin
	 * @param settings
	 */
	protected static void initialize(DatabasePlugin plugin, Settings settings) {
		Database.plugin = plugin;
		Database.settings = settings;
		Database.connect();
	}

	/**
	 * Any plugin that extends DatabasePlugin needs to register with this method.
	 * This will also give the initial condition of the plugin.
	 *
	 * @param pl
	 */
	public static void register(DatabasePlugin pl) {
		if (registeredPlugins == null) {
			registeredPlugins = new HashSet<DatabasePlugin>();
		}
		registeredPlugins.add(pl);
		pl.setActive(Database.plugin.isActive());
	}

	/**
	 * Attempts to connect to the database, given that the database is in a
	 * disabled/disconnected state.
	 *
	 * If successful, notifies all registered plugins.
	 */
	protected static void connect() {
		// Must be in a disabled state
		if (!plugin.isActive()) {
			// Grab the URL and user/pass
			try {
				connection = DriverManager.getConnection(settings.getURL(), settings.getURL(), settings.getPassword());
				plugin.setActive(true);
				plugin.getLogger().info("Connected to the database!");
			} catch (SQLException e) {
				plugin.setActive(false);
				plugin.getLogger().severe("Could not connect to the database!");
			}
			// Now update all registered plugins
			for (DatabasePlugin pl : registeredPlugins) {
				pl.setActive(plugin.isActive());
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
		if (plugin.isActive()) {
			try {
				connection.close();
				plugin.getLogger().info("Disconnected successfully!");
			} catch (SQLException e) {
				plugin.getLogger().severe("Disconnection failure!");
			}
			// Update all plugins
			plugin.setActive(false);
			for (DatabasePlugin pl : registeredPlugins) {
				pl.setActive(plugin.isActive());
			}
		}
	}

}
