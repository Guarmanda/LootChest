package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class ToggleFallCommand extends SubCommand {
	
	public ToggleFallCommand() {
		super("togglefall", Arrays.asList(ArgType.LOOTCHEST));
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		Lootchest lc = Main.getInstance().getLootChest().get(args[1]);
		boolean fall = lc.getFall();
		if (fall) {
			lc.setFall(false);
			Utils.msg(sender, "disabledFallEffect", Constants.cheststr, args[1]);
		} else {
			lc.setFall(true);
			Utils.msg(sender, "enabledFallEffect", Constants.cheststr, args[1]);
		}
		lc.updateData();
	}
}
