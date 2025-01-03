package fr.black_eyes.lootchest.commands.commands;

import java.util.Collections;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Mat;
import fr.black_eyes.lootchest.LootChestUtils;
import fr.black_eyes.lootchest.commands.ArgType;
import fr.black_eyes.lootchest.commands.SubCommand;
import fr.black_eyes.lootchest.ui.UiHandler;
import fr.black_eyes.simpleJavaPlugin.Utils;

public class CreateCommand extends SubCommand {
	
	private final UiHandler uiHandler;
	public CreateCommand() {
		super("create", Collections.singletonList(ArgType.STRING));
		setPlayerRequired(true);

		this.uiHandler = Main.getInstance().getUiHandler();
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Block chest;
		String chestName = args[1];
		chest = LootChestUtils.getWatchedBlock(player);
		if (!Mat.isALootChestBlock(chest)) {
			Utils.msg(sender, "notAChest", " ", " ");
		} else if (LootChestUtils.isEmpty(((InventoryHolder) chest.getState()).getInventory())) {
			Utils.msg(sender, "chestIsEmpy", " ", " ");
		} else if (Main.getInstance().getLootChest().containsKey(chestName)) {
			Utils.msg(sender, "chestAlreadyExist", Constants.CHEST_PLACEHOLDER, chestName);
		} else if (LootChestUtils.isLootChest(chest.getLocation()) != null) {
			Utils.msg(sender, "blockIsAlreadyLootchest", Constants.CHEST_PLACEHOLDER, chestName);
		} else {
			Lootchest newChest = new Lootchest(chest, chestName);
			Main.getInstance().getLootChest().put(chestName, newChest);
			newChest.spawn(true);
			newChest.updateData();
			Utils.msg(sender, "chestSuccefulySaved", Constants.CHEST_PLACEHOLDER, chestName);
			uiHandler.openUi(player, UiHandler.UiType.MAIN, newChest);
		}
	}
}
