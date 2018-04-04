package com.github.pocketkid2.database;

import org.bukkit.scheduler.BukkitRunnable;

public class ValidationTask extends BukkitRunnable {

	private DatabasePlugin plugin;
	private int timeout;
	private boolean status;

	public ValidationTask(DatabasePlugin plugin, int timeout) {
		this.plugin = plugin;
		this.timeout = timeout;
		status = true;
	}

	@Override
	public void run() {
		// Only check if the connection is supposedly online
		if (plugin.isOnline()) {
			// Call the API method
			if (Database.validate(timeout)) {
				// Only display the success message once after the last failure
				if (!status) {
					plugin.getLogger().info("Connection validation successful!");
				}
				// Always set the status to true after a successful validation
				status = true;
			} else {
				// This happens when we thought we were online but we aren't. This is the key
				// outcome, so always display
				plugin.getLogger().info("Connection validation failed! The server is offline!");
				// And always set status to false
				status = false;
				// And forcefully make sure we are disconnected
				Database.disconnect();
			}
		} else {
			// If this is the first check after a disconnect, let the console know
			if (status) {
				plugin.getLogger().info("Server is offline, ignoring validation check until server comes back online!");
				status = false;
			}
		}
	}

}
