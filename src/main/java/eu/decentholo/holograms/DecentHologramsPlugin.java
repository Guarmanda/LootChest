package eu.decentholo.holograms;

import org.bukkit.plugin.java.JavaPlugin;

import eu.decentholo.holograms.api.DecentHolograms;
import eu.decentholo.holograms.api.DecentHologramsAPI;

public class DecentHologramsPlugin {


	public void onLoad(JavaPlugin plugin) {
		DecentHologramsAPI.onLoad(plugin);
	}


	public DecentHolograms onEnable() {
		DecentHologramsAPI.onEnable();
		return DecentHologramsAPI.get();
	}


	public void onDisable() {
		DecentHologramsAPI.onDisable();
	}

}
