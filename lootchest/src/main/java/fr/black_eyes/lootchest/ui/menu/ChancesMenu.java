package fr.black_eyes.lootchest.ui.menu;

import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.LootChestUtils;
import fr.black_eyes.lootchest.ui.ChestUi;
import fr.black_eyes.lootchest.ui.UiHandler;
import fr.black_eyes.simpleJavaPlugin.Utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

/**
 * A menu to change the chances of each item to be spawned in a loot chest
 */
// (for the compatibility with 1.7, we need deprecated code)
@SuppressWarnings("deprecation")
public class ChancesMenu extends ChestUi {

	private final Lootchest chest;
	private final UiHandler uiHandler;

	public ChancesMenu(Lootchest chest, UiHandler uiHandler) {
		super(3, LootChestUtils.getMenuName("chances", chest.getName()));
		this.chest = chest;
		this.uiHandler = uiHandler;
	}

	@Override
	public ChestUi open(Player player) {
		loadChestItems();
		return super.open(player);
	}

	/**
	 * Displays all chest items in the UI with their respective chances
	 */
	private void loadChestItems() {
		Inventory chestInv = chest.getInv();
		String lore = Utils.getMsg("Menu.chances.lore", "[Chest]", chest.getName());

		for (int i = 0; i < chestInv.getSize(); i++) {
			if (chestInv.getItem(i) == null || Objects.requireNonNull(chestInv.getItem(i)).getType() == Material.AIR) {
				continue;
			}
			ItemStack item = Objects.requireNonNull(chestInv.getItem(i)).clone();
			renameItem(item, item.getItemMeta().getDisplayName(), lore + "||" + chest.getChances()[i] + "%");
			setItem(i, item, null);
		}
	}

	@Override
	public boolean onClickSlot(Player player, int slot, ClickType type) {
		ItemStack item = getItem(slot);

		if (item == null || item.getType() == Material.AIR) {
			return true;
		}
		ItemMeta meta = item.getItemMeta();
		if (!meta.hasLore()) {
			return true;
		}
		Integer chance = chest.getChances()[slot];
		//adds or subtracts the chance based on the click type
		switch (type) {
			case MIDDLE:
				chance /= 2;
				break;
			case SHIFT_LEFT:
				chance += 10;
				break;
			case SHIFT_RIGHT:
				chance -= 10;
				break;
			case LEFT:
				chance += 1;
				break;
			case RIGHT:
				chance -= 1;
				break;
			default:
				return true;
		}
		//updates the item's chance and the item in the UI
		chance = Math.max(1, Math.min(100, chance));
		List<String> lore = meta.getLore();
        if (lore != null) {
            lore.set(lore.size() - 1, chance + "%");
        }
        meta.setLore(lore);
		item.setItemMeta(meta);
		chest.setChance(slot, chance);
		return true;
	}

	@Override
	public void onClose(Player player) {
		uiHandler.openUi(player, UiHandler.UiType.MAIN, chest, 2);
	}
}
