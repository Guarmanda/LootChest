package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MaxFilledSlotsCommand extends SubCommand {
	
	public MaxFilledSlotsCommand() {
		super("maxfilledslots", 0);
		setPlayerRequired(true);
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		Lootchest lc = Main.getInstance().getLootChest().get(args[0]);
		if (lc == null){
			Utils.msg(sender, "chestDoesntExist", Constants.cheststr, args[0]);
			return;
		}
		int maxFilledSlots;
		try {
			maxFilledSlots = Integer.parseInt(args[1]);
		}catch (NumberFormatException e) {
			return;
		}
		if(maxFilledSlots >= 0) {
			lc.setMaxFilledSlots(maxFilledSlots);
		}
		lc.updateData();
		Utils.msg(sender, "editedMaxFilledSlots", Constants.cheststr, lc.getName());
	}
	
	@Override
	public String getUsage() {
		return "/lc maxfilledslots <chestname> <slotnumber>";
	}
}
