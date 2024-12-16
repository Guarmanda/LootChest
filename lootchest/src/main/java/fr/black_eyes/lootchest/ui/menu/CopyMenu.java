package fr.black_eyes.lootchest.ui.menu;

import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Mat;
import fr.black_eyes.lootchest.LootChestUtils;
import fr.black_eyes.lootchest.ui.PagedChestUi;
import fr.black_eyes.lootchest.ui.UiHandler;
import fr.black_eyes.simpleJavaPlugin.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fr.black_eyes.simpleJavaPlugin.Files;

import static fr.black_eyes.lootchest.Constants.DATA_CHEST_PATH;

/**
 * A menu to copy the properties of another loot chest
 */
public class CopyMenu extends PagedChestUi {
	
	private final Lootchest chest;
	private final UiHandler uiHandler;

	public CopyMenu(Lootchest chest, UiHandler uiHandler) {
		super(6, LootChestUtils.getMenuName("copy", chest.getName()));
		this.chest = chest;
		this.uiHandler = uiHandler;

		Files configFiles = Main.getInstance().getConfigFiles();
		List<Lootchest> chests = new ArrayList<>(Main.getInstance().getLootChest().values());
		chests.sort(Comparator.comparing(Lootchest::getName));
		
		//list all chests that can be copied
		for (Lootchest otherChest : chests) {
			if (otherChest.equals(chest)) {
				continue;
			}
			String holoName = otherChest.getHolo();
			String otherName = otherChest.getName();
			String effect = configFiles.getData().getString(DATA_CHEST_PATH + otherName + ".particle");
			String world;
			if (Bukkit.getWorld(otherChest.getWorld()) == null) {
				world = "Unloaded world";
			} else {
				world = Bukkit.getWorld(otherChest.getWorld()).getName();
			}
			ItemStack item = nameItem(Mat.CHEST, Utils.color("&6" + otherName), 1, Utils.color("&bHologram: &6" + holoName + "||&bWorld: &6" + world + "||&bEffect: &6" + effect));
			addContent(item, p -> copyChest(p, otherChest));
		}
	}

	public void copyChest(Player player, Lootchest copyChest) {
		LootChestUtils.copychest(copyChest, chest);
		Main.getInstance().getLootChest().get(chest.getName()).updateData();
		Utils.msg(player, "copiedChest", "[Chest1]", copyChest.getName(), "[Chest2]", chest.getName());
	}

	@Override
	public void onClose(Player player) {
		uiHandler.openUi(player, UiHandler.UiType.MAIN, chest, 2);
	}
}
