package fr.black_eyes.lootchest.commands.commands;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.commands.ArgType;
import fr.black_eyes.lootchest.commands.SubCommand;
import fr.black_eyes.simpleJavaPlugin.Utils;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class DespawnCommand extends SubCommand {

	public DespawnCommand() {
		super("despawn", Collections.singletonList(ArgType.LOOTCHEST));
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		String chestName = args[1];
		Lootchest lc = Main.getInstance().getLootChest().get(chestName);
		if(lc.isGoodType(lc.getActualLocation().getBlock())) {
			lc.spawn(false, true);
			Utils.msg(sender, "ChestDespawned", Constants.CHEST_PLACEHOLDER, chestName);
		}else
			Utils.msg(sender, "NoChestAtLocation", " ", " ");
	}
}
