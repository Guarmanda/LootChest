package fr.black_eyes.lootchest.particles;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Objects;

@SuppressWarnings("deprecation")
public interface ParticleData {

    static ParticleData of(ItemStack item) {
        return new AbstractParticleData(Objects.requireNonNull(item, "item"));
    }

    static BlockData createBlockData(Material material) {
        return createBlockData(material, (byte) 0);
    }

    static BlockData createBlockData(Material material, byte legacyData) {
        Objects.requireNonNull(material, "material");
        return new BlockData(new MaterialData(material, legacyData));

    }

    static DustOptions createDustOptions(Color color, float size) {
        Objects.requireNonNull(color, "color");


        return new DustOptions(new Particle.DustOptions(color, size));
    }

    static DustTransition createDustTransition(Color fromColor, Color toColor, float size) {
          return DustTransition.EMPTY;
    }

    class AbstractParticleData implements ParticleData {
        final Object data;

        private AbstractParticleData(Object data) {
            this.data = data;
        }
    }

    class BlockData extends AbstractParticleData {
        private BlockData(Object data) {
            super(data);
        }
    }

    class DustOptions extends AbstractParticleData {
        private DustOptions(Object data) {
            super(data);
        }
    }

    class DustTransition extends DustOptions {
        private static final DustTransition EMPTY = new DustTransition(null);

        private DustTransition(Object data) {
            super(data);
        }
    }
}
