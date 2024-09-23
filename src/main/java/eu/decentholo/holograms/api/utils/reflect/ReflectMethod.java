package eu.decentholo.holograms.api.utils.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import fr.black_eyes.lootchest.Main;

public class ReflectMethod {

	private final Class<?> clazz;
	private final String name;
	private final Class<?>[] parameterTypes;

	private Method method;

	public ReflectMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		this.clazz = clazz;
		this.name = name;
		this.parameterTypes = parameterTypes;
	}

	private void init() {
		if (method != null) return;
		try {
			try {
				method = clazz.getDeclaredMethod(name, parameterTypes);
			} catch (NoSuchMethodException e) {
				method = clazz.getMethod(name, parameterTypes);
			}
			method.setAccessible(true);
		} catch (NoSuchMethodException e) {
			Main.getInstance().getLogger().severe("Could not find method "+name+" in class "+clazz.getName());
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T invoke(Object instance, Object... args) {
		this.init();

		Object object = null;
		try {
			object = method.invoke(instance, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			Main.getInstance().getLogger().severe("Could not invoke method "+name+" in class "+clazz.getName());
		}
		return object == null ? null : (T) object;
	}

	@SuppressWarnings("unchecked")
	public <T> T invokeStatic(Object... args) {
		this.init();

		Object object = null;
		try {
			object = method.invoke(null, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			Main.getInstance().getLogger().severe("Could not invoke static method "+name+" in class "+clazz.getName());
		}
		return object == null ? null : (T) object;
	}

}
