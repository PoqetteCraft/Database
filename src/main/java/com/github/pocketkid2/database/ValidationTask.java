package com.github.pocketkid2.database;

import org.bukkit.scheduler.BukkitRunnable;

public class ValidationTask extends BukkitRunnable {

	private DatabasePlugin plugin;
	private int timeout;
	private boolean status;

	public ValidationTask(DatabasePlugin plugin, int timeout) {
		this.plugin = plugin;
		this.timeout = timeout;
	}

	@Override
	public void run() {
		if (plugin.isOnline()) {
			if (Database.validate(timeout)) {
				plugin.getLogger().info("Connection validation successful!");
				status = true;
			} else {
				plugin.getLogger().info("Connection validation failed! The server is offline!");
				status = false;
				Database.disconnect();
			}
		} else {
			if (status) {
				plugin.getLogger().info("Server is offline, ignoring validation check until server comes back online!");
				status = false;
			}
		}
	}

}
