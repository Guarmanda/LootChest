package eu.decentholo.holograms.api.utils.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import fr.black_eyes.lootchest.Main;


public class ReflectConstructor {

    private final Class<?> clazz;
    private final Class<?>[] parameterTypes;

    private Constructor<?> constructor;

    public ReflectConstructor(Class<?> clazz, Class<?>... parameterTypes) {
        this.clazz = clazz;
        this.parameterTypes = parameterTypes;
    }

    private void init() {
        if (constructor != null) return;
        try {
            constructor = clazz.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            Main.getInstance().getLogger().severe("Failed to find constructor for class %s with parameter types "+ clazz.getName() + " "+ Arrays.toString(parameterTypes));
        }
    }

    public <T> T newInstance(Object... args) {
        this.init();

        Object object = null;
        try {
            object = constructor.newInstance(args);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            Main.getInstance().getLogger().severe("Failed to create new instance of class %s with parameter types "+ clazz.getName() +" "+ Arrays.toString(parameterTypes));
        }
        return object == null ? null : (T) object;
    }

}
