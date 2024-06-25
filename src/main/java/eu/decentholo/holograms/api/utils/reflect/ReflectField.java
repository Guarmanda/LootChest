package eu.decentholo.holograms.api.utils.reflect;

import java.lang.reflect.Field;

import fr.black_eyes.lootchest.Main;

public class ReflectField<T> {

	private final Class<?> clazz;
	private final String name;

	private Field field;

	public ReflectField(Class<?> clazz, String name) {
		this.clazz = clazz;
		this.name = name;
	}

	public ReflectField(Field field) {
		this.field = field;
		this.clazz = field.getDeclaringClass();
		this.name = field.getName();

		this.field.setAccessible(true);
	}

	private void init() throws Exception {
		if (field == null) {
			try {
				field = clazz.getDeclaredField(name);
			} catch (Exception e) {
				field = clazz.getField(name);
			}
			field.setAccessible(true);
		}
	}

	@SuppressWarnings("unchecked")
	public T getValue(Object object) {
		try {
			this.init();
			return (T) field.get(object);
		} catch (Exception e) {
			Main.getInstance().getLogger().severe("Failed to get field value: "+ e +  " "+ name);
			return null;
		}
	}

	public void setValue(Object object, Object value) {
		try {
			this.init();
			field.set(object, value);
		} catch (Exception e) {
			Main.getInstance().getLogger().severe("Failed to set field value: "+ e + " "+ name);
		}
	}

}
