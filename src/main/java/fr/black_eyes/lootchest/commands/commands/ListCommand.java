package fr.black_eyes.lootchest.commands.commands;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.commands.SubCommand;
import fr.black_eyes.simpleJavaPlugin.Utils;

public class ListCommand extends SubCommand {
	
	public ListCommand() {
		super("list");
	}
	
	@Override
	public String getUsage() {
		return "/lc list";
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		List<String> sorted = Main.getInstance().getLootChest().keySet().stream().sorted().collect(Collectors.toList());
		Utils.msg(sender, "ListCommand", "[List]", String.join(" ", sorted));
	}
}
