package fr.black_eyes.lootchest.commands.commands;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.LootChestUtils;
import fr.black_eyes.lootchest.commands.SubCommand;
import fr.black_eyes.simpleJavaPlugin.Utils;

public class GetNameCommand extends SubCommand {
	
	public GetNameCommand() {
		super("getname");
		setPlayerRequired(true);
		
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Block chest;
		BlockIterator iter = new BlockIterator(player, 10);
		Block lastBlock = iter.next();
		while (iter.hasNext()) {
			lastBlock = iter.next();
			if (lastBlock.getType() == Material.AIR) {
				continue;
			}
			break;
		}
		chest = lastBlock;
		Lootchest lc = LootChestUtils.isLootChest(chest.getLocation());
		
		if (lc != null) {
			Utils.msg(sender, "commandGetName", Constants.cheststr, lc.getName());
			return;
		}
		Utils.msg(sender, "notAChest", " ", " ");
	}
}
