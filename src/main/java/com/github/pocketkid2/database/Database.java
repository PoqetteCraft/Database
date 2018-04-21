package com.github.pocketkid2.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * The main API class, used as a singleton. All API calls should be made through
 * this.
 *
 * @author Adam
 *
 */
public final class Database {

	// The instance of the main class, used for determining current status
	private static DatabasePlugin plugin;
	// A list of all registered plugins
	private static Set<Plugin> registeredPlugins;
	// The database connection
	private static Connection connection;
	// The values for connection
	private static Settings settings;

	private static void enable(Plugin plugin) {
		Bukkit.getPluginManager().enablePlugin(plugin);
	}

	private static void disable(Plugin plugin) {
		Bukkit.getPluginManager().disablePlugin(plugin);
	}

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
		Database.registeredPlugins = new HashSet<Plugin>();
		Database.connect();
	}

	/*
	 * INTERNAL API
	 *
	 * Returns a set of all registered plugins
	 *
	 */
	protected static Set<Plugin> getRegisteredPlugins() {
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
			plugin.getLogger().severe(Messages.Console.TIMEOUT_INVALID);
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
				plugin.getLogger().info(Messages.Console.CONNECT_SUCCESS);
			} catch (SQLException e) {
				plugin.setOnline(false);
				plugin.getLogger().severe(Messages.Console.CONNECT_FAILURE);
			}
			// Now update all registered plugins
			for (Plugin pl : registeredPlugins) {
				enable(pl);
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
				plugin.getLogger().info(Messages.Console.DISCONNECT_SUCCESS);
			} catch (SQLException e) {
				plugin.getLogger().severe(Messages.Console.DISCONNECT_FAILURE);
			}
			// Update all plugins
			plugin.setOnline(false);
			for (Plugin pl : registeredPlugins) {
				disable(pl);
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
	 * @return true if the plugin is enabled, false if it had to be disabled
	 */
	public static boolean register(Plugin pl) {
		registeredPlugins.add(pl);
		if (plugin.isOnline()) {
			plugin.getLogger().info(pl.getName() + " has been registered");
			return true;
		} else {
			plugin.getLogger().warning(pl.getName() + " has been registered but will be disabled because the server is offline");
			disable(pl);
			return false;
		}
	}

	/**
	 * PUBLIC API
	 *
	 * Creates a prepared statement with the given SQL string and returns it
	 *
	 * @param sql
	 *            A string with the full statement (question marks for variables)
	 * @return The prepared statement (or null if the connection is invalid
	 */
	public static PreparedStatement prepare(String sql) {
		if (connection != null) {
			try {
				return connection.prepareStatement(sql);
			} catch (SQLException e) {
				plugin.getLogger().severe(Messages.Console.PREPARED_STATEMENT_FAILURE);
				return null;
			}
		} else {
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
		if (connection != null) {
			try {
				return connection.createStatement();
			} catch (SQLException e) {
				plugin.getLogger().severe(Messages.Console.STATEMENT_FAILURE);
				return null;
			}
		} else {
			return null;
		}
	}

}
