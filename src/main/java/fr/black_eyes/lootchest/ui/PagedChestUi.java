package fr.black_eyes.lootchest.ui;

import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class PagedChestUi extends ChestUi {
	
	private final List<Map.Entry<ItemStack, Consumer<Player>>> contents;
	
	public PagedChestUi(int rows, String title) {
		super(rows, title);
		this.contents = new ArrayList<>();
	}
	
	protected void addContent(ItemStack item, Consumer<Player> action) {
		contents.add(new AbstractMap.SimpleEntry<>(item, action));
	}
	
	private int getPageCount() {
		int particleCount = Main.getInstance().getParticles().size() + 9;
		int pageSize = (getRows() - 1) * 9;
		return (int) Math.max(1, Math.ceil((float) particleCount / pageSize));
	}
	
	private void loadPage(int pageIdx) {
		clear();
		int pageCount = getPageCount();
		int pageSize = (getRows() - 1) * 9;
		
		if (pageIdx < 0 || pageIdx >= pageCount) {
			return;
		}
		int currentSlot = 0;
		int currentItemIdx = pageIdx * pageSize;
		
		int prevPageSlot = getRows() * 9 - 9;
		int nextPageSlot = getRows() * 9 - 1;
		
		while (currentItemIdx < contents.size() && currentSlot < pageSize) {
			Map.Entry<ItemStack, Consumer<Player>> content = contents.get(currentItemIdx);
			if (content.getKey() != null) {
				setItem(currentSlot, content.getKey(), content.getValue());
			}
			currentItemIdx++;
			currentSlot++;
		}
		if (pageIdx > 0) {
			String prevPageLabel = Utils.getMsg("Menu.particles.page", "[Number]", "" + pageIdx);
			setItem(prevPageSlot, nameItem(Material.PAPER, prevPageLabel), p -> loadPage(pageIdx - 1));
		}
		if (pageIdx < pageCount - 1) {
			String nextPageLabel = Utils.getMsg("Menu.particles.page", "[Number]", "" + (pageIdx + 2));
			setItem(nextPageSlot, nameItem(Material.PAPER, nextPageLabel), p -> loadPage(pageIdx + 1));
		}
	}
	
	@Override
	public ChestUi open(Player player) {
		loadPage(0);
		return super.open(player);
	}
}
