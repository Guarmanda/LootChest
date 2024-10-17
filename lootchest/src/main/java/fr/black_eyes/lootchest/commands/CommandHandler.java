package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Main;
import fr.black_eyes.simpleJavaPlugin.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles the execution of a command and delegates all logic to registered subcommands
 */
public class CommandHandler implements CommandExecutor, TabCompleter {
	
	private final Set<SubCommand> commands;
	
	/**
	 * @param baseCommand name of the plugin command to handle (the command directly after the /)
	 */
	public CommandHandler(JavaPlugin plugin, String baseCommand) {
		this.commands = new HashSet<>();
		plugin.getCommand(baseCommand).setExecutor(this);
		plugin.getCommand(baseCommand).setTabCompleter(this);
	}
	
	public void addSubCommand(SubCommand command) {
		commands.add(command);
	}
	
	/**
	 * Looks up which subcommand was called as first argument and executes it
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length < 1) {
			displayhelp(sender);
			return true;
		}
		String subCmdName = args[0];
		
		for (SubCommand command : commands) {
			if (command.matchesAlias(subCmdName)) {
				if (!hasPerm(sender, command.getPermission())) {
					return true;
				}
				command.execute(sender, args);
				return true;
			}
		}
		displayhelp(sender);
		return true;
	}
	
	/**
	 * Looks up which subcommand was tabbed as first argument and returns its tab list
	 */
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		String subCommandName = args[0].toLowerCase();
		
		if (args.length == 1) {
			return commands.stream()
					.map(SubCommand::getName)
					.filter(s -> s.startsWith(subCommandName))
					.collect(Collectors.toList());
		}
		for (SubCommand command : commands) {
			if (!command.matchesAlias(subCommandName)) {
				continue;
			}
			if (command.getPermission() != null && !sender.hasPermission(command.getPermission())) {
				continue;
			}
			if (command.getArgCount() < args.length - 1) {
				continue;
			}
			String lastArg = args[args.length - 1].toLowerCase();
			return command.getTabList(args).stream()
					.filter(s -> s.toLowerCase().startsWith(lastArg))
					.collect(Collectors.toList());
		}
		return new ArrayList<>();
	}
	
	public void displayhelp(CommandSender p) {
		List<String> help = Main.getInstance().getConfigFiles().getLang().getStringList("help");
		for (int i = 0; i < help.size(); i++) {
			p.sendMessage(Utils.color(help.get(i)));
		}
	}
	
	private boolean hasPerm(CommandSender sender, String permission) {
		if (!sender.hasPermission(permission) && !sender.hasPermission("lootchest.admin") && !sender.hasPermission("lootchest.*")) {
			Utils.msg(sender, "noPermission", "[Permission]", "lootchest." + permission);
			return false;
		}
		return true;
	}
}
