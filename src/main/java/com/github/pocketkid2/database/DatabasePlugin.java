package com.github.pocketkid2.database;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class DatabasePlugin extends JavaPlugin {

	private static final int TICKS_PER_SECOND = 20;

	// Whether the connection is alive or not
	private boolean online;

	// The settings class
	private Settings settings;

	// The scheduled validation task
	private BukkitTask task;

	@Override
	public void onEnable() {
		// On startup, we are not connected
		online = false;
		// Create the config file if it doesn't exist
		saveDefaultConfig();
		// Populate the settings class
		settings = new Settings(getConfig());
		// Register the command
		getCommand("database").setExecutor(new DatabaseCommand(this));
		// Initialize the database with the settings in the config
		Database.initialize(this, settings);
		// Check if the connection was established
		if (online) {
			// Schedule the validation task
			ValidationTask vt = new ValidationTask(this, settings.getTimeout());
			task = vt.runTaskTimerAsynchronously(this, 0, settings.getRepeat() * TICKS_PER_SECOND);
			// Log status
			getLogger().info("Connected and ready to register plugins!");
		} else {
			getLogger().warning("Not connected; Please check server and connection values");
		}
	}

	@Override
	public void onDisable() {
		// Cancel the validation task
		if (task != null) {
			task.cancel();
		}
		// Disconnect if need be
		if (online) {
			Database.disconnect();
		}
		// We're done
		getLogger().info("Done!");
	}

	/**
	 * Returns true if the plugin is enabled/active/ready to work
	 *
	 * For the base plugin, this is used to determine if the connection is alive
	 *
	 * For extended plugins, use this when deciding if certain plugin features are
	 * allowed to operate
	 *
	 * @return
	 */
	protected boolean isOnline() {
		return online;
	}

	/**
	 * Changes the current activation status as returned by isActive()
	 *
	 * @param active
	 */
	protected void setOnline(boolean online) {
		this.online = online;
	}
}
