package fr.black_eyes.lootchest.commands.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import fr.black_eyes.lootchest.commands.SubCommand;


public class LocateCommand extends SubCommand {
	
	public LocateCommand() {
		super("locate");
	}
	
	@Override
	public String getUsage() {
		return "/lc locate";
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		Utils.msg(sender, "locate_command.main_message", " ", " ");
		for (Lootchest lcs : Main.getInstance().getLootChest().values()) {
			if (lcs.getRespawn_natural() && !lcs.getTaken()) {
				Location block = lcs.getActualLocation();
				String holo = lcs.getHolo();
				Utils.msg(sender, "locate_command.chest_list", "[world]", block.getWorld().getName(), Constants.cheststr, holo, "[x]", block.getX() + "", "[y]", block.getY() + "", "[z]", block.getZ() + "");
			}
		}
	}
	

}
