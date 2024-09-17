package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetTimeCommand extends SubCommand {
	
	public SetTimeCommand() {
		super("settime", 2);
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		String chestName = args[0];
		Lootchest lc = Main.getInstance().getLootChest().get(chestName);
		if (lc == null){
			Utils.msg(sender, "chestDoesntExist", Constants.cheststr, chestName);
			return;
		}
		lc.setTime(Integer.parseInt(args[1]));
		lc.updateData();
		lc.spawn( true);
		Utils.msg(sender, "settime", Constants.cheststr, chestName);
	}
	
	@Override
	public String getUsage() {
		return "/lc settime <chestname> <seconds>";
	}
}
