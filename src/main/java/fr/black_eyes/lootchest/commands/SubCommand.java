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
 * Abstract class for implementing logic for a sub command
 */
public abstract class SubCommand {
	
	private final String name;
	private final int argCount;
	private final Set<String> aliases;
	private String permission;
	private boolean isPlayerRequired;
	
	Map<UUID, Runnable> executeCallbacks;
	Set<UUID> asyncExecutes;
	
	public SubCommand(String name, int argCount) {
		this.name = name.toLowerCase();
		this.argCount = argCount;
		this.permission = "lootchest." + name;
		aliases = new HashSet<>();
		aliases.add(this.name);
		asyncExecutes = new HashSet<>();
		executeCallbacks = new HashMap<>();
	}
	
	public String getPermission() {
		return permission;
	}
	
	public int getArgCount() {
		return argCount;
	}
	
	public void setPlayerRequired(boolean value) {
		this.isPlayerRequired = value;
	}
	
	public void execute(CommandSender sender, String[] args) {
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
	 * Method to overwrite to hanlde sub command execution with arguments
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
	
	public abstract String getUsage();
	
	public List<String> getTabList(String[] args) {
		return new LinkedList<>();
	}
}
