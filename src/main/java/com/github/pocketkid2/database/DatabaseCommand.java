package com.github.pocketkid2.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class DatabaseCommand implements CommandExecutor {

	private DatabasePlugin plugin;

	public DatabaseCommand(DatabasePlugin pl) {
		plugin = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			if (label.equalsIgnoreCase("connect")) {
				connect(sender);
			} else if (label.equalsIgnoreCase("disconnect")) {
				disconnect(sender);
			} else if (label.equalsIgnoreCase("reconnect")) {
				reconnect(sender);
			} else {
				// Show status/info to user
				PluginDescriptionFile pdf = plugin.getDescription();
				sender.sendMessage(pdf.getFullName());
				sender.sendMessage("Author: " + String.join(", ", pdf.getAuthors()));
				sender.sendMessage("Current database status: " + (plugin.isOnline() ? (ChatColor.GREEN + "online") : (ChatColor.RED + "offline")));
				return false;
			}
		} else if (args.length == 1) {
			// Read argument
			if (args[0].equalsIgnoreCase("connect") || args[0].equalsIgnoreCase("con")) {
				connect(sender);
			} else if (args[0].equalsIgnoreCase("disconnect") || args[0].equalsIgnoreCase("discon")) {
				disconnect(sender);
			} else if (args[0].equalsIgnoreCase("reconnect") || args[0].equalsIgnoreCase("recon")) {
				reconnect(sender);
			} else if (args[0].equalsIgnoreCase("plugins") || args[0].equalsIgnoreCase("pl")) {
				plugins(sender);
			} else {
				sender.sendMessage(Messages.Command.UNKNOWN_ARG(args[0]));
				return false;
			}
		} else {
			sender.sendMessage(Messages.Command.TOO_MANY_ARGS);
			return false;
		}
		return true;
	}

	private void reconnect(CommandSender sender) {
		// Check current state
		if (!plugin.isOnline()) {
			sender.sendMessage(Messages.Command.DATABASE_ALREADY_OFFLINE);
		} else {
			// Attempt reconnection
			Database.disconnect();
			Database.connect();
			// Read off results
			if (plugin.isOnline()) {
				sender.sendMessage(Messages.Command.RECONNECT_SUCCESS);
			} else {
				sender.sendMessage(Messages.Command.RECONNECT_FAILURE);
			}
		}
	}

	private void disconnect(CommandSender sender) {
		// Check current state
		if (!plugin.isOnline()) {
			sender.sendMessage(Messages.Command.DATABASE_ALREADY_OFFLINE);
		} else {
			// Disconnect
			Database.disconnect();
			sender.sendMessage(Messages.Command.DISCONNECT_SUCCESS);
		}
	}

	private void connect(CommandSender sender) {
		// Check current state
		if (plugin.isOnline()) {
			sender.sendMessage(Messages.Command.DATABASE_ALREADY_ONLINE);
		} else {
			// Attempt connection
			Database.connect();
			// And display the results
			if (plugin.isOnline()) {
				sender.sendMessage(Messages.Command.CONNECT_SUCCESS);
			} else {
				sender.sendMessage(Messages.Command.CONNECT_FAILURE);
			}
		}
	}

	private void plugins(CommandSender sender) {
		// Display list of currently registered plugins
		StringBuilder sb = new StringBuilder();
		Set<JavaPlugin> plugins = Database.getRegisteredPlugins();
		sb.append(String.format("Database Plugins (%d): ", plugins.size()));
		List<String> names = new ArrayList<String>();
		for (JavaPlugin p : plugins) {
			names.add((p.isEnabled() ? (ChatColor.GREEN) : (ChatColor.RED)) + p.getName());
		}
		sb.append(String.join(ChatColor.RESET + ", ", names));
		sender.sendMessage(sb.toString());
	}

}
