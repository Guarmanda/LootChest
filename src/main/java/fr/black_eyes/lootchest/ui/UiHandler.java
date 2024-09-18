package fr.black_eyes.lootchest.ui;

import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.ui.menu.ChancesMenu;
import fr.black_eyes.lootchest.ui.menu.ContentsMenu;
import fr.black_eyes.lootchest.ui.menu.CopyMenu;
import fr.black_eyes.lootchest.ui.menu.MainMenu;
import fr.black_eyes.lootchest.ui.menu.ParticleMenu;
import fr.black_eyes.lootchest.ui.menu.TimeMenu;
import fr.black_eyes.lootchest.ui.menu.TypeMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Class that creates and manages command UI that can be opened by right-clicking the maze wand.
 */
public class UiHandler {
	
	private final Map<UUID, ChestUi> playerUis;
	private final JavaPlugin plugin;

	public enum UiType {
		MAIN, COPY, TYPE, PARTICLE, EDIT, TIME, CHANCES;

	}
	public UiHandler(JavaPlugin plugin) {
		this.playerUis = new HashMap<>();
		this.plugin = plugin;
	}

	public boolean hasPlayerOpenUi(Player player) {
		return playerUis.containsKey(player.getUniqueId());
	}

	public boolean handleClick(Player player, int slot, ClickType type) {
		UUID playerId = player.getUniqueId();
		return playerUis.get(playerId).onClickSlot(player, slot, type);
	}

	public void handleClose(Player player) {
		UUID playerId = player.getUniqueId();
		playerUis.get(playerId).onClose(player);
		playerUis.remove(playerId);
	}

	public void openUi(Player player, UiType type, Lootchest chest, long delay) {
		new BukkitRunnable() {
			@Override
			public void run() {
				openUi(player, type, chest);
			}
		}.runTaskLater(plugin, delay);
	}

	public void openUi(Player player, UiType type, Lootchest chest) {
		UUID playerId = player.getUniqueId();
		switch (type) {
			case MAIN:
				playerUis.put(playerId, new MainMenu(chest, this).open(player));
				break;
			case COPY:
				playerUis.put(playerId, new CopyMenu(chest, this).open(player));
				break;
			case TYPE:
				playerUis.put(playerId, new TypeMenu(chest, this).open(player));
				break;
			case PARTICLE:
				playerUis.put(playerId, new ParticleMenu(chest, this).open(player));
				break;
			case EDIT:
				playerUis.put(playerId, new ContentsMenu(chest, this).open(player));
				break;
			case TIME:
				playerUis.put(playerId, new TimeMenu(chest, this).open(player));
				break;
			case CHANCES:
				playerUis.put(playerId, new ChancesMenu(chest, this).open(player));
		}
	}
	
	public void remove(UUID playerId) {
		playerUis.remove(playerId);
	}
}
