package fr.black_eyes.lootchest.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Takes care of executing any registered command when it is being called
 */
public class CommandHandler implements CommandExecutor, TabCompleter {
	
	private final Set<SimpleCommand> commands;
	
	public CommandHandler(JavaPlugin plugin, String baseCommand) {
		this.commands = new HashSet<>();
		plugin.getCommand(baseCommand).setExecutor(this);
		plugin.getCommand(baseCommand).setTabCompleter(this);
	}
	
	public void addSubCommand(SimpleCommand command) {
		commands.add(command);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String cmdName = cmd.getName();
		
		for (SimpleCommand command : commands) {
			if (command.matchesAlias(cmdName)) {
				command.execute(sender, args);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		for (SimpleCommand command : commands) {
			if (!command.matchesAlias(cmd.getName())) {
				continue;
			}
			if (command.getPermission() != null && !sender.hasPermission(command.getPermission())) {
				continue;
			}
			return command.getTabList(args);
		}
		return null;
	}
}
