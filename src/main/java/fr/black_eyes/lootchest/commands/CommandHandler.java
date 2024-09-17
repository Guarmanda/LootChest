package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Takes care of executing any registered command when it is being called
 */
public class CommandHandler implements CommandExecutor, TabCompleter {
	
	private final Set<SubCommand> commands;
	
	public CommandHandler(JavaPlugin plugin, String baseCommand) {
		this.commands = new HashSet<>();
		plugin.getCommand(baseCommand).setExecutor(this);
		plugin.getCommand(baseCommand).setTabCompleter(this);
	}
	
	public void addSubCommand(SubCommand command) {
		commands.add(command);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length < 1) {
			displayhelp(sender);
			return true;
		}
		String subCmdName = args[0];
		
		for (SubCommand command : commands) {
			if (command.matchesAlias(subCmdName)) {
				command.execute(sender, Arrays.copyOfRange(args, 1, args.length));
				return true;
			}
		}
		displayhelp(sender);
		return true;
	}
	
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
			if (command.getArgCount() < args.length-1) {
				continue;
			}
			String lastArg = args[args.length - 1].toLowerCase();
			return command.getTabList(Arrays.copyOfRange(args, 0, args.length-1)).stream()
					.filter(s -> s.toLowerCase().startsWith(lastArg))
					.collect(Collectors.toList());
		}
		return new ArrayList<>();
	}
	
	public void displayhelp(CommandSender p) {
		List<String> help = Main.getInstance().getConfigFiles().getLang().getStringList("help");
		for(int i=0; i<help.size();i++) {
			p.sendMessage(Utils.color(help.get(i)));
		}
	}
}
