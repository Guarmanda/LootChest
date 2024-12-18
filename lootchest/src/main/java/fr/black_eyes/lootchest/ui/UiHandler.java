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
 * Handles the opening and closing of UIs for players and delegating interactions with the UIs
 */
public class UiHandler {
	
	private final Map<UUID, ChestUi> playerUis;
	private final JavaPlugin plugin;

	/**
	 * Enum for the different types of UIs that can be opened
	 */
	public enum UiType {
		MAIN, COPY, TYPE, PARTICLE, EDIT, TIME, CHANCES
	}

	public UiHandler(JavaPlugin plugin) {
		this.playerUis = new HashMap<>();
		this.plugin = plugin;
	}

	/**
	 * Returns true if the player is listed as currently having a UI open
	 */
	public boolean hasPlayerOpenUi(Player player) {
		return playerUis.containsKey(player.getUniqueId());
	}

	/**
	 * Delegates the click event to the respective UI to handle the player's interaction with the UI
	 * @return true if the click event should be cancelled
	 */
	public boolean handleClick(Player player, int slot, ClickType type) {
		UUID playerId = player.getUniqueId();
		return playerUis.get(playerId).onClickSlot(player, slot, type);
	}

	/**
	 * Inform the UI that the player has closed the UI
	 */
	public void handleClose(Player player) {
		UUID playerId = player.getUniqueId();
		playerUis.get(playerId).onClose(player);
		playerUis.remove(playerId);
	}

	/**
	 * Opens the specified UI for the player with a delay
	 * @param delay ticks to wait before opening the UI
	 */
	public void openUi(Player player, UiType type, Lootchest chest, long delay) {
		new BukkitRunnable() {
			@Override
			public void run() {
				openUi(player, type, chest);
			}
		}.runTaskLater(plugin, delay);
	}

	/**
	 * Opens the specified UI for the player
	 */
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

}
