package fr.black_eyes.lootchest.menu;

import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Mat;
import fr.black_eyes.lootchest.Utils;
import fr.black_eyes.lootchest.particles.Particle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ParticleMenu extends PagedChestUi {
	
	Lootchest chest;
	
	public ParticleMenu(Lootchest chest) {
		super(6, Utils.getMenuName("particles", chest.getName()));
		this.chest = chest;
		
		for (int i = 0; i < 9; ++i) {
			if (i == 4) {
				addContent(nameItem(Mat.BARRIER, "Disable particles"), p -> changeParticle(chest, null, p));
			} else {
				addContent(null, null);
			}
		}
		for (Particle particle : Main.getInstance().getParticles().values()) {
			addContent(nameItem(particle.getMat(), particle.getReadableName()), p -> changeParticle(chest, particle, p));
		}
	}
	
	private void changeParticle(Lootchest chest, Particle particle, Player player) {
		chest.setParticle(particle);
		chest.updateData();
		Location loc = chest.getParticleLocation();
		Main.getInstance().getPart().put(loc, particle);
		Utils.msg(player, "editedParticle", "[Chest]", chest.getName());
	}
}
