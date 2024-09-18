package fr.black_eyes.lootchest.menu;

import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Mat;
import fr.black_eyes.lootchest.Utils;
import fr.black_eyes.lootchest.particles.Particle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ParticleMenu extends ChestUi {
	
	Lootchest chest;
	
	public ParticleMenu(Lootchest chest) {
		super(6, Utils.getMenuName("particles", chest.getName()));
		this.chest = chest;
		loadPage(0);
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
		int currentSlot = pageIdx == 0 ? 9 : 0;
		int currentParticleIdx = pageIdx == 0 ? 0 : pageIdx * pageSize - 9;
		
		ArrayList<Particle> particles = new ArrayList<>(Main.getInstance().getParticles().values());
		int prevPageSlot = (getRows() - 1) * 9;
		int nextPageSlot = getRows() * 9 - 1;
		
		while (currentParticleIdx < particles.size() && currentSlot < pageSize) {
			Particle particle = particles.get(currentParticleIdx);
			setItem(currentSlot, nameItem(particle.getMat(), particle.getReadableName()), p -> changeParticle(chest, particle, p));
			currentParticleIdx++;
			currentSlot++;
		}
		if (pageIdx == 0) {
			setItem(4, nameItem(Mat.BARRIER, "Disable particles"), p -> changeParticle(chest, null, p));
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
	
	private void changeParticle(Lootchest chest, Particle particle, Player player) {
		chest.setParticle(particle);
		chest.updateData();
		Location loc = chest.getParticleLocation();
		Main.getInstance().getPart().put(loc, particle);
		Utils.msg(player, "editedParticle", "[Chest]", chest.getName());
	}
}
