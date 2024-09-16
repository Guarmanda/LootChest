package fr.black_eyes.lootchest.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A command with no defined way of execution (not arguments or sub commands)
 */
public abstract class SimpleCommand {
	
	private final String name;
	private final int argCount;
	private final Set<String> aliases;
	private String permission;
	private boolean isPlayerRequired;
	
	Map<UUID, Runnable> executeCallbacks;
	Set<UUID> asyncExecutes;
	
	public SimpleCommand(String name, int argCount) {
		this.name = name.toLowerCase();
		this.argCount = argCount;
		aliases = new HashSet<>();
		aliases.add(this.name);
		
		asyncExecutes = new HashSet<>();
		executeCallbacks = new HashMap<>();
	}
	
	public String getPermission() {
		return permission;
	}
	
	/**
	 * Sets a permission required to execute the command
	 *
	 * @param permission name of permission
	 */
	public SimpleCommand setPermission(String permission) {
		this.permission = permission;
		return this;
	}
	
	public SimpleCommand setPlayerRequired(boolean value) {
		this.isPlayerRequired = value;
		return this;
	}
	
	/**
	 * Executes the implemented functionality of this command if permissions and player requirements are met
	 * Runs the callback after finishing, which might be asynchronously later.
	 *
	 * @param sender being that executed this command
	 * @param args   further arguments input behind the command name
	 */
	public void execute(CommandSender sender, String[] args) {
		if (getPermission() != null && !sender.hasPermission(getPermission())) {
			sender.sendMessage(ChatColor.RED + "You do not have the permission for this command!");
			return;
		}
		if (isPlayerRequired && !(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Please, run this command in-game");
			return;
		}
		if (args.length < argCount) {
			sender.sendMessage(ChatColor.RED + getUsage());
			return;
		}
		onCommand(sender, args);
	}
	
	/**
	 * Executes the functionality of the command
	 *
	 * @param sender being that executed this command
	 * @param args   further arguments input after the command name
	 */
	protected abstract void onCommand(CommandSender sender, String[] args);
	
	public String getName() {
		return name;
	}
	
	public void addAlias(String alias) {
		aliases.add(alias.toLowerCase());
	}
	
	public boolean matchesAlias(String alias) {
		return aliases.contains(alias);
	}
	
	/**
	 * Returns the pattern how to use this command
	 */
	public abstract String getUsage();
	
	public void sendUsage(CommandSender sender) {
		sender.sendMessage(getUsage());
	}
	
	public List<String> getTabList(String[] arguments) {
		return new LinkedList<>();
	}
}
