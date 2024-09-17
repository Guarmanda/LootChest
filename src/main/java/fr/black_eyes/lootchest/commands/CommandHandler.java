package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
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
		if (args.length == 1) {
			return commands.stream()
					.map(SubCommand::getName)
					.filter(s -> s.startsWith(args[0]))
					.collect(Collectors.toList());
		}
		for (SubCommand command : commands) {
			if (!command.matchesAlias(cmd.getName())) {
				continue;
			}
			if (command.getPermission() != null && !sender.hasPermission(command.getPermission())) {
				continue;
			}
			return command.getTabList(args).stream()
					.filter(s -> s.startsWith(args[args.length - 1]))
					.collect(Collectors.toList());
		}
		return null;
	}
	
	public void displayhelp(CommandSender p) {
		List<String> help = Main.getInstance().getConfigFiles().getLang().getStringList("help");
		for(int i=0; i<help.size();i++) {
			p.sendMessage(Utils.color(help.get(i)));
		}
	}
}
