package fr.black_eyes.lootchest.particles;

import org.bukkit.Bukkit;

import eu.decentholo.holograms.api.utils.reflect.Version;

import java.util.Optional;

/**
 * Small reflection class to use CraftBukkit and NMS
 *
 * @author MrMicky
 */
public final class FastReflection {

    public static final String OBC_PACKAGE = "org.bukkit.craftbukkit";
    public static final String NMS_PACKAGE = "net.minecraft.server";

    public static final String VERSION =
    //NSM versions aren't checkable anymore since 1.20.6. But we can still use this for old versions
        Particle.getVersion() < 20 ?
            ( Bukkit.getServer().getClass().getSimpleName().equals("CraftServer")
            ? Bukkit.getServer().getClass().getPackage().getName().substring(OBC_PACKAGE.length() + 1)
            : "unknown")
    // now for new ones:
        : Version.getCurrentVersion().name();

    private FastReflection() {
        throw new UnsupportedOperationException();
    }

    public static String nmsClassName(String className) {
        return NMS_PACKAGE + '.' + VERSION + '.' + className;
    }

    public static Class<?> nmsClass(String className) throws ClassNotFoundException {
        return Class.forName(nmsClassName(className));
    }

    public static Optional<Class<?>> nmsOptionalClass(String className) {
        return optionalClass(nmsClassName(className));
    }

    public static String obcClassName(String className) {
        return OBC_PACKAGE + '.' + VERSION + '.' + className;
    }

    public static Class<?> obcClass(String className) throws ClassNotFoundException {
        return Class.forName(obcClassName(className));
    }

    public static Optional<Class<?>> optionalClass(String className) {

        try {
            return Optional.of(Class.forName(className));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

	public static Object enumValueOf(Class<?> enumClass, String enumName) {
        return Enum.valueOf(enumClass.asSubclass(Enum.class), enumName);
    }
}
