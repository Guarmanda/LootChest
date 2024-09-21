package fr.black_eyes.lootchest.commands.commands;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import fr.black_eyes.lootchest.commands.ArgType;
import fr.black_eyes.lootchest.commands.SubCommand;

public class SetTimeCommand extends SubCommand {
	
	public SetTimeCommand() {
		super("settime", Arrays.asList(ArgType.LOOTCHEST, ArgType.INTEGER));
	}
	
	@Override
	public String getUsage() {
		return "/lc settime <chestname> <seconds>";
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		String chestName = args[1];
		Lootchest lc = Main.getInstance().getLootChest().get(chestName);
		lc.setTime(Integer.parseInt(args[1]));
		lc.updateData();
		lc.spawn(true);
		Utils.msg(sender, "settime", Constants.cheststr, chestName);
	}
	
}
