package fr.black_eyes.lootchest.ui.menu;

import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Mat;
import fr.black_eyes.lootchest.LootChestUtils;
import fr.black_eyes.lootchest.ui.ChestUi;
import fr.black_eyes.lootchest.ui.UiHandler;
import fr.black_eyes.simpleJavaPlugin.Utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A menu to change the material of a loot chest
 */
public class TypeMenu extends ChestUi {

	private final Lootchest chest;
	private final UiHandler uiHandler;

	public TypeMenu(Lootchest chest, UiHandler uiHandler) {
		super(1, LootChestUtils.getMenuName("type", chest.getName()));
		this.chest = chest;
		this.uiHandler = uiHandler;

		setItem(0, new ItemStack(Mat.CHEST, 1), p -> changeChestType(p, Mat.CHEST));
		setItem(1, new ItemStack(Mat.TRAPPED_CHEST, 1), p -> changeChestType(p, Mat.TRAPPED_CHEST));
		if (Mat.BARREL != Mat.CHEST) {
			setItem(3, new ItemStack(Mat.BARREL), p -> changeChestType(p, Mat.BARREL));
		}
	}

	void changeChestType(Player player, Material type) {
		chest.setType(type);
		chest.updateData();
		chest.spawn(true);
		Utils.msg(player, "editedChestType", "[Chest]", chest.getName());
	}

	@Override
	public void onClose(Player player) {
		uiHandler.openUi(player, UiHandler.UiType.MAIN, chest, 2);
	}
}
