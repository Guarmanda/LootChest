package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class ListCommand extends SubCommand {
	
	public ListCommand() {
		super("list", 0);
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
