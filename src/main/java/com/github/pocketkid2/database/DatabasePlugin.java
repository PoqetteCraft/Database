package com.github.pocketkid2.database;

import org.bukkit.plugin.java.JavaPlugin;

public class DatabasePlugin extends JavaPlugin {

	// Whether the connection is alive or not
	private boolean online;

	@Override
	public void onEnable() {
		// On startup, we are not connected
		online = false;
		// Create the config file if it doesn't exist
		saveDefaultConfig();
		// Initialize the database with the settings in the config
		Database.initialize(this, new Settings(getConfig()));
		// Register the command
		getCommand("database").setExecutor(new DatabaseCommand(this));
		// We're done
		getLogger().info("Done!");
	}

	@Override
	public void onDisable() {
		// Disconnect if need be
		if (online) {
			Database.disconnect();
		}
		// We're done
		getLogger().info("Done!");
	}

	/*
	 * Returns true if the plugin is enabled/active/ready to work
	 *
	 * For the base plugin, this is used to determine if the connection is alive
	 *
	 * For extended plugins, use this when deciding if certain plugin features are
	 * allowed to operate
	 *
	 */
	public boolean isOnline() {
		return online;
	}

	/*
	 * Changes the current activation status as returned by isActive()
	 *
	 */
	protected void setOnline(boolean online) {
		this.online = online;
	}
}
