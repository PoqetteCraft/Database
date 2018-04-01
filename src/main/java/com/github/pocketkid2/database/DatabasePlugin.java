package com.github.pocketkid2.database;

import org.bukkit.plugin.java.JavaPlugin;

public class DatabasePlugin extends JavaPlugin {

	// Whether the connection is alive or not
	private boolean active;

	@Override
	public void onEnable() {
		getLogger().info("Done!");
	}

	@Override
	public void onDisable() {
		getLogger().info("Done!");
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
