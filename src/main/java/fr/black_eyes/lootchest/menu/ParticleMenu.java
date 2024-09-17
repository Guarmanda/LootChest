package fr.black_eyes.lootchest.menu;

import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Mat;
import fr.black_eyes.lootchest.Utils;
import fr.black_eyes.lootchest.particles.Particle;
import org.bukkit.Material;

import java.util.ArrayList;

public class ParticleMenu extends ChestUi {
	
	Lootchest chest;
	
	public ParticleMenu(Lootchest chest) {
		super(6, Utils.getMenuName("particles", chest.getName()));
		this.chest = chest;
	}
	
	private int getPageCount() {
		int particleCount = Main.getInstance().getParticles().size() + 9;
		int pageSize = getRows() * 9 - 1;
		return (int) Math.max(1, Math.ceil((float) particleCount / pageSize));
	}
	
	private void loadPage(int pageIdx) {
		clear();
		int pageCount = getPageCount();
		int currentSlot = pageIdx == 1 ? 9 : 0;
		int currentParticleIdx = pageIdx * (getRows() * 9 - 1) - 9;
		
		ArrayList<Particle> particles = new ArrayList<>(Main.getInstance().getParticles().values());
		int prevPageSlot = (getRows() - 1) * 9;
		int nextPageSlot = getRows() * 9 - 1;
		
		if (pageIdx == 0) {
			setItem(4, nameItem(Mat.BARRIER, "Disable particles"), p -> chest.setParticle(null));
		}
		while (true) {
			if (currentParticleIdx > particles.size() - 1 || currentSlot > nextPageSlot) {
				break;
			}
			if (currentSlot == prevPageSlot && pageIdx > 0) {
				String prevPageLabel = Utils.getMsg("Menu.particles.page", "[Number]", "" + pageIdx);
				setItem(currentSlot, nameItem(Material.PAPER, prevPageLabel), player -> loadPage(pageIdx - 1));
			} else if (currentSlot == nextPageSlot && pageIdx < pageCount - 1) {
				String nextPageLabel = Utils.getMsg("Menu.particles.page", "[Number]", "" + (pageIdx + 2));
				setItem(currentSlot, nameItem(Material.PAPER, nextPageLabel), player -> loadPage(pageIdx - 1));
			} else {
				Particle particle = particles.get(currentParticleIdx);
				setItem(currentSlot, nameItem(particle.getMat(), particle.getReadableName()), p -> chest.setParticle(particle));
			}
			currentSlot++;
		}
	}
}
