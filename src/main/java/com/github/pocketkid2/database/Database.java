package com.github.pocketkid2.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
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
	 * INTERNAL API
	 *
	 * Called by the first instance of DatabasePlugin, used to initialize this
	 * Database instance with config values and such
	 *
	 */
	protected static void initialize(DatabasePlugin plugin, Settings settings) {
		Database.plugin = plugin;
		Database.settings = settings;
		Database.registeredPlugins = new HashSet<DatabasePlugin>();
		Database.connect();
	}

	/*
	 * INTERNAL API
	 *
	 * Returns a set of all registered plugins
	 *
	 */
	protected static Set<DatabasePlugin> getRegisteredPlugins() {
		return registeredPlugins;
	}

	/*
	 * INTERNAL API
	 *
	 * Queries the server to see if it is alive
	 *
	 */
	protected static boolean validate(int timeout) {
		try {
			return connection.isValid(timeout);
		} catch (SQLException e) {
			plugin.getLogger().severe("Timeout value is less than 0!");
			return false;
		}
	}

	/*
	 * INTERNAL API
	 *
	 * Attempts to connect to the database, given that the database is in a
	 * disabled/disconnected state.
	 *
	 * If successful, notifies all registered plugins
	 *
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

	/*
	 * INTERNAL API
	 *
	 * Disconnects from the database, assuming that the connection is live.
	 *
	 * Notifies all registered plugins
	 *
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

	/**
	 * PUBLIC API
	 *
	 * Any plugin that extends DatabasePlugin needs to register with this method.
	 * This will also give the initial condition of the plugin.
	 *
	 * @param pl
	 */
	public static void register(DatabasePlugin pl) {
		registeredPlugins.add(pl);
		pl.setOnline(Database.plugin.isOnline());
	}

	/**
	 * PUBLIC API
	 *
	 * Creates a prepared statement with the given SQL string and returns it
	 *
	 * @param sql
	 *            A string with the full statement (question marks for variables)
	 * @return The prepared statement
	 */
	public static PreparedStatement prepare(String sql) {
		try {
			return connection.prepareStatement(sql);
		} catch (SQLException e) {
			plugin.getLogger().severe("Error creating prepared statement, please check connection!");
			return null;
		}
	}

	/**
	 * PUBLIC API
	 *
	 * Creates a regular statement and returns it
	 *
	 * @return The statement
	 */
	public static Statement create() {
		try {
			return connection.createStatement();
		} catch (SQLException e) {
			plugin.getLogger().severe("Error creating statement, please check connection!");
			return null;
		}
	}

}
