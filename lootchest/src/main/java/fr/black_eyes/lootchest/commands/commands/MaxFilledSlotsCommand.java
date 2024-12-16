package fr.black_eyes.lootchest.commands.commands;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.commands.ArgType;
import fr.black_eyes.lootchest.commands.SubCommand;
import fr.black_eyes.simpleJavaPlugin.Utils;

public class MaxFilledSlotsCommand extends SubCommand {
	
	public MaxFilledSlotsCommand() {
		super("maxfilledslots", Arrays.asList(ArgType.LOOTCHEST, ArgType.INTEGER));
	}
	
	@Override
	public String getUsage() {
		return "/lc maxfilledslots <chestname> <numberOfSlots>";
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		Lootchest lc = Main.getInstance().getLootChest().get(args[1]);
		int maxFilledSlots = Integer.parseInt(args[2]);
		if (maxFilledSlots >= 0) {
			lc.setMaxFilledSlots(maxFilledSlots);
		}
		lc.updateData();
		Utils.msg(sender, "editedMaxFilledSlots", Constants.CHEST_PLACEHOLDER, lc.getName());
	}
}
