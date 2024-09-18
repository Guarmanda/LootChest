package fr.black_eyes.lootchest.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
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
	
	@Getter private String name;
	private Set<String> aliases;
	@Getter private String permission;
	@Setter private boolean isPlayerRequired = false;
	@Getter private List<ArgType> requiredArgs;
	@Getter private List<ArgType> optionalArgs;
	
	Map<UUID, Runnable> executeCallbacks;
	Set<UUID> asyncExecutes;

	public SubCommand(String name) {
		init(name, new ArrayList<>(), new ArrayList<>());
	}
	
	public SubCommand(String name, List<ArgType> requiredArgs) {
		init(name, requiredArgs, new ArrayList<>());
	}

	public SubCommand(String name, List<ArgType> requiredArgs, List<ArgType> OptionnalArgs) {
		init(name, requiredArgs, OptionnalArgs);
	}

	private void init(String name, List<ArgType> requiredArgs, List<ArgType> OptionnalArgs) {
		this.name = name.toLowerCase();
		this.requiredArgs = requiredArgs;
		this.permission = "lootchest." + name;
		aliases = new HashSet<>();
		aliases.add(this.name);
		asyncExecutes = new HashSet<>();
		executeCallbacks = new HashMap<>();
		this.optionalArgs = OptionnalArgs;
	}

	public int getArgCount() {
		return requiredArgs.size() + optionalArgs.size();
	}

	private List<ArgType> getArgs() {
		List<ArgType> args = new ArrayList<>();
		args.addAll(requiredArgs);
		args.addAll(optionalArgs);
		return args;
	}
	
	public void execute(CommandSender sender, String[] args) {
		if (isPlayerRequired && !(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Please, run this command in-game");
			return;
		}
		if (args.length < getArgCount()) {
			sender.sendMessage(ChatColor.RED + getUsage());
			return;
		}
		// check each arguments
		for (int i = 1; i < args.length; i++) {
			ArgType arg = getArgs().get(i-1);
			if (!arg.isValid(args[i], sender)) {
				//sender.sendMessage(ChatColor.RED + "Invalid argument " + args[i] + " for " + arg.getName());
				return;
			}
		}
		onCommand(sender, args);
	}
	
	/**
	 * Method to overwrite to hanlde sub command execution with arguments
	 */
	protected abstract void onCommand(CommandSender sender, String[] args);
	
	public void addAlias(String alias) {
		aliases.add(alias.toLowerCase());
	}
	
	public boolean matchesAlias(String alias) {
		return aliases.contains(alias);
	}
	
	public String getUsage(){
		return "/lc " + name + " " + requiredArgs.stream().map(ArgType::getName).reduce((a, b) -> "<"+a + "> <" + b+"> ").orElse("") + optionalArgs.stream().map(ArgType::getName).reduce((a, b) -> "["+a + "] [" + b+"] ").orElse("");
	}
	
	public List<String> getTabList(String[] args) {
		switch(getArgs().get(args.length - 2)) {
			case LOOTCHEST:
				return new ArrayList<>(Main.getInstance().getLootChest().keySet());
			case PLAYER:
				return Utils.getPlayersOnline().stream().map(Player::getName).collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
			case WORLD:
				return Bukkit.getWorlds().stream().map(w -> w.getName()).collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
			case INTEGER:
			case STRING:
			default:
				return new ArrayList<>();
		}
	}
}
