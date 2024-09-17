package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListCommand extends SubCommand {
	
	public ListCommand() {
		super("list", 0);
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		Utils.msg(sender, "ListCommand", "[List]", String.join(" ", Main.getInstance().getLootChest().keySet()));
	}
	
	@Override
	public String getUsage() {
		return "/lc list";
	}
}
