package fr.black_eyes.lootchest.commands.commands;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.LootChestUtils;
import fr.black_eyes.lootchest.commands.ArgType;
import fr.black_eyes.lootchest.commands.SubCommand;
import fr.black_eyes.simpleJavaPlugin.Utils;

public class CopyCommand extends SubCommand {
	
	public CopyCommand() {
		super("copy", Arrays.asList(ArgType.LOOTCHEST, ArgType.LOOTCHEST));
	}
	
	//worth the override to make a better explaination
	@Override
	public String getUsage() {
		return "/lc copy <from chest> <to chest>";
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		Lootchest lc = Main.getInstance().getLootChest().get(args[1]);
		Lootchest copy = Main.getInstance().getLootChest().get(args[2]);
		LootChestUtils.copychest(lc, copy);
		copy.updateData();
		Utils.msg(sender, "copiedChest", "[Chest1]", lc.getName(), "[Chest2]", copy.getName());
	}
}
