package com.github.pocketkid2.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import net.md_5.bungee.api.ChatColor;

public class DatabaseCommand implements CommandExecutor {

	private DatabasePlugin plugin;

	public DatabaseCommand(DatabasePlugin pl) {
		plugin = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			// Show status/info to user
			PluginDescriptionFile pdf = plugin.getDescription();
			sender.sendMessage(pdf.getFullName());
			sender.sendMessage("Author: " + String.join(", ", pdf.getAuthors()));
			sender.sendMessage("Current database status: " + (plugin.isOnline() ? (ChatColor.GREEN + "online") : (ChatColor.RED + "offline")));
			return false;
		} else if (args.length == 1) {
			// Read argument
			if (args[0].equalsIgnoreCase("connect") || args[0].equalsIgnoreCase("con")) {
				// Check current state
				if (plugin.isOnline()) {
					sender.sendMessage("The database is already online!");
				} else {
					// Attempt connection
					Database.connect();
					// And display the results
					if (plugin.isOnline()) {
						sender.sendMessage("Connection successful!");
					} else {
						sender.sendMessage("Connection unsuccessful!");
					}
				}
			} else if (args[0].equalsIgnoreCase("disconnect") || args[0].equalsIgnoreCase("discon")) {
				// Check current state
				if (!plugin.isOnline()) {
					sender.sendMessage("The database is already offline!");
				} else {
					// Disconnect
					Database.disconnect();
					sender.sendMessage("Disconnected!");
				}
			} else if (args[0].equalsIgnoreCase("reconnect") || args[0].equalsIgnoreCase("recon")) {
				// Check current state
				if (!plugin.isOnline()) {
					sender.sendMessage("The database is offline! Try /" + label + " connect");
				} else {
					// Attempt reconnection
					Database.disconnect();
					Database.connect();
					// Read off results
					if (plugin.isOnline()) {
						sender.sendMessage("Reconnection successful!");
					} else {
						sender.sendMessage("Reconnection unsuccessful!");
					}
				}
			} else if (args[0].equalsIgnoreCase("plugins") || args[0].equalsIgnoreCase("pl")) {
				// Display list of currently registered plugins
				StringBuilder sb = new StringBuilder();
				Set<DatabasePlugin> plugins = Database.getRegisteredPlugins();
				sb.append(String.format("Database Plugins (%d): ", plugins.size()));
				List<String> names = new ArrayList<String>();
				for (DatabasePlugin p : plugins) {
					names.add((p.isOnline() ? (ChatColor.GREEN) : (ChatColor.RED)) + p.getName());
				}
				sb.append(String.join(ChatColor.RESET + ", ", names));
				sender.sendMessage(sb.toString());
			} else {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

}
