package fr.black_eyes.lootchest.menu;

import fr.black_eyes.lootchest.Mat;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ChestUi {
	
	private final String title;
	private final int rows;
	private final Inventory inventory;
	private final Map<Integer, Consumer<Player>> clickActions;
	
	
	public ChestUi(int rows, String title) {
		this.title = title;
		this.rows = rows;
		inventory = Bukkit.createInventory(null, rows * 9, title);
		clickActions = new HashMap<>();
	}
	
	public int getRows() {
		return rows;
	}
	
	public void setItem(int slot, ItemStack item, Consumer<Player> clickAction) {
		if (slot < 0 || slot >= rows * 9) {
			throw new IllegalArgumentException(String.format("%d is outside 0 and %d inventory slots", slot, rows * 9 - 1));
		}
		inventory.setItem(slot, item);
		clickActions.put(slot, clickAction);
	}
	
	public void changeItem(int slot, ItemStack item) {
		inventory.setItem(slot, item);
	}
	
	public void onClickSlot(Player player, int slot) {
		if (clickActions.containsKey(slot)) {
			clickActions.get(slot).accept(player);
		}
	}
	
	public void open(Player player) {
		player.openInventory(inventory);
	}
	
	public void clear() {
		inventory.clear();
		clickActions.clear();
	}
	
	@Override
	protected ChestUi clone() {
		ChestUi clone = new ChestUi(rows, title);
		clone.inventory.setContents(inventory.getContents());
		clone.clickActions.putAll(clickActions);
		return clone;
	}
	
	protected ItemStack nameItem(Material mat, String name) {
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}
	
	protected ItemStack getToggleItem(String path, Boolean name) {
		if (name) {
			return nameItem(Mat.EMERALD_BLOCK, Utils.getMsg("Menu.main.disable_" + path));
		} else {
			return nameItem(Mat.REDSTONE_BLOCK, Utils.getMsg("Menu.main.enable_" + path));
		}
	}
}
