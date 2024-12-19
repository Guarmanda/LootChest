package fr.black_eyes.lootchest.particles;

public interface ParticleData {


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

        private DustTransition(Object data) {
            super(data);
        }
    }
}
