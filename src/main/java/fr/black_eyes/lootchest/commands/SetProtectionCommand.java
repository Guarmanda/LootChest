package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class SetProtectionCommand extends SubCommand {
	
	public SetProtectionCommand() {
		super("setprotection", Arrays.asList(ArgType.LOOTCHEST, ArgType.INTEGER));
	}
	
	@Override
	public String getUsage() {
		return "/lc setprotection <chestname> <seconds>";
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		String chestName = args[1];
		Lootchest lc = Main.getInstance().getLootChest().get(chestName);
		lc.setProtectionTime(Integer.parseInt(args[1]));
		lc.updateData();
		Utils.msg(sender, "editedProtectionTime", Constants.cheststr, chestName);
	}
	
}
