package eu.decentholo.holograms.api.utils.config;

import eu.decentholo.holograms.api.utils.Log;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * This utility class has some methods for loading/saving values between {@link YamlConfiguration} and Objects.
 *
 * @author d0by
 */
@UtilityClass
public final class CFG {


    /**
     * Save the given Object into a new {@link ConfigurationSection}. Values saved into the configuration are
     * taken from the fields annotated with {@link ConfigValue} which defines the path in the configuration.
     * <br>
     * If the object is a {@link Class}, static fields are used.
     *
     * @param object The object.
     * @return The configuration.
     */
    @NotNull
    public static ConfigurationSection saveIntoConfigurationFromObject(@NotNull Object object) {
        return saveIntoConfigurationFromObject(object, new YamlConfiguration());
    }

    /**
     * Save the given Object into the given {@link ConfigurationSection}. Values saved into the configuration are
     * taken from the fields annotated with {@link ConfigValue} which defines the path in the configuration.
     * <br>
     * If the object is a {@link Class}, static fields are used.
     *
     * @param object The object.
     * @param config The ConfigurationSection.
     * @return The configuration.
     */
    @NotNull
    public static ConfigurationSection saveIntoConfigurationFromObject(@NotNull Object object, @NotNull ConfigurationSection config) {
        boolean stat = object instanceof Class<?>;
        Class<?> clazz = stat ? (Class<?>) object : object.getClass();
        for (Field f : clazz.getDeclaredFields()) {
            if (!f.isAnnotationPresent(Key.class)) {
                continue;
            }
            f.setAccessible(true);

            // -- Get the annotation
            Key configValue = f.getAnnotation(Key.class);
            String key = configValue.value();

            // -- Save the value
            try {
                Object newValue;
                if (Modifier.isStatic(f.getModifiers())) {
                    newValue = f.get(null);
                } else if (!stat) {
                    newValue = f.get(object);
                } else {
                    newValue = null;
                }
                Object currentValue = config.get(key);
                if (currentValue != null && currentValue.equals(newValue)) {
                    continue;
                }
                config.set(key, newValue);
            } catch (Exception e) {
                Log.warn("Failed to save field value to configuration. (%s)", e, f.getName());
            }
        }
        return config;
    }

    /**
     * Load values from the given {@link ConfigurationSection} into the given Objects fields. Value for a field
     * is only loaded if the field is annotated with {@link ConfigValue} which defines the path to use
     * to locate the value in the configuration.
     * <br>
     * If the object is a {@link Class}, static fields are used.
     *
     * @param object The object.
     * @param config The configuration.
     */
    public static void loadFromConfigurationToObject(@NotNull Object object, @NotNull ConfigurationSection config) {
        boolean stat = object instanceof Class<?>;
        Class<?> clazz = stat ? (Class<?>) object : object.getClass();
        for (Field f : clazz.getDeclaredFields()) {
            if (!f.isAnnotationPresent(Key.class)) {
                continue;
            }
            f.setAccessible(true);

            // -- Get the annotation
            Key configValue = f.getAnnotation(Key.class);
            String key = configValue.value();
            double min = configValue.min();
            double max = configValue.max();

            try {
                Object o = config.get(key);
                if (o != null) {
                    // -- If number, check min & max
                    if (o instanceof Number) {
                        Number number = (Number) o;
                        Class<?> fType = f.getType();
                        if (fType.isAssignableFrom(int.class)) {
                            int numberValue = number.intValue();
                            CFG.setFieldValue(f, object, (int) Math.min(Math.max(numberValue, min), max));
                        } else if (fType.isAssignableFrom(float.class)) {
                            float numberValue = number.floatValue();
                            CFG.setFieldValue(f, object, (float) Math.min(Math.max(numberValue, min), max));
                        } else if (fType.isAssignableFrom(double.class)) {
                            double numberValue = number.doubleValue();
                            CFG.setFieldValue(f, object, Math.min(Math.max(numberValue, min), max));
                        } else if (fType.isAssignableFrom(short.class)) {
                            short numberValue = number.shortValue();
                            CFG.setFieldValue(f, object, (short) Math.min(Math.max(numberValue, min), max));
                        } else if (fType.isAssignableFrom(byte.class)) {
                            byte numberValue = number.byteValue();
                            CFG.setFieldValue(f, object, (byte) Math.min(Math.max(numberValue, min), max));
                        }
                        continue;
                    }
                    // -- Set the field value
                    CFG.setFieldValue(f, object, o);
                }
            } catch (Exception e) {
                Log.warn("Failed to load field value from configuration. (%s)", e, f.getName());
            }
        }
    }

    private static void setFieldValue(@NotNull Field f, Object parent, Object o) throws IllegalAccessException {
        if (Modifier.isStatic(f.getModifiers())) {
            f.set(null, o);
        } else if (!(parent instanceof Class<?>)) {
            f.set(parent, o);
        }
    }

}