package fr.black_eyes.lootchest.ui.menu;

import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Mat;
import fr.black_eyes.lootchest.Utils;
import fr.black_eyes.lootchest.particles.Particle;
import fr.black_eyes.lootchest.ui.PagedChestUi;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ParticleMenu extends PagedChestUi {
	
	Lootchest chest;
	
	public ParticleMenu(Lootchest chest) {
		super(6, Utils.getMenuName("particles", chest.getName()));
		this.chest = chest;
		
		//add an empty row with only the "Disable particle" item in the mid
		for (int i = 0; i < 9; ++i) {
			if (i == 4) {
				addContent(nameItem(Mat.BARRIER, "Disable particles"), p -> changeParticle(chest, null, p));
			} else {
				addContent(null, null);
			}
		}
		//add items for all other particle effects
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
