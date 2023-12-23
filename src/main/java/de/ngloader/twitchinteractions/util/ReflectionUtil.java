package de.ngloader.twitchinteractions.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;

public class ReflectionUtil {

	private static final String MINECRAFTSERVER_PATH = "net.minecraft.server";
	private static final String CRAFTBUKKIT_PATH = "org.bukkit.craftbukkit";

	private static final String SERVER_VERSION;

	static {
		String version = Bukkit.getServer().getClass().getName().substring(ReflectionUtil.CRAFTBUKKIT_PATH.length());
		SERVER_VERSION = version.substring(0, version.length() - "CraftServer".length());
	}

	public static String getMinecraftServer(String className) {
		return ReflectionUtil.MINECRAFTSERVER_PATH + ReflectionUtil.SERVER_VERSION + className;
	}

	public static Class<?> getMinecraftServerClass(String className) {
		try {
			return Class.forName(ReflectionUtil.getMinecraftServer(className));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getCraftBukkit(String className) {
		return ReflectionUtil.CRAFTBUKKIT_PATH + ReflectionUtil.SERVER_VERSION + className;
	}

	public static Class<?> getCraftBukkitClass(String className) {
		try {
			return Class.forName(ReflectionUtil.getCraftBukkit(className));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Class<?> getClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void setField(Object instance, String name, Object value) {
		ReflectionUtil.setField(instance.getClass(), instance, name, value);
	}

	public static void setField(Class<?> clazz, Object instance, String name, Object value) {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			field.set(instance, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Object getField(Object instance, String name) {
		return ReflectionUtil.getField(instance.getClass(), instance, name);
	}

	public static Object getField(Class<?> clazz, Object obj, String name) {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			return field.get(obj);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Field getField(String className, String name) {
		try {
			return ReflectionUtil.getField(Class.forName(className), name);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Field getField(Class<?> clazz, String name) {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Field getFieldByType(Class<?> clazz, Class<?> target, int index) {
		int currentIndex = 0;
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getType().equals(target)) {
				if (currentIndex == index) {
					field.setAccessible(true);
					return field;
				}
				currentIndex++;
			}
		}

		throw new NullPointerException(String.format("Unable to find field by class %s in class %s with index %d",
				clazz.getSimpleName(),
				target.getSimpleName(),
				index));
	}

	public static Method getMethod(String className, String name, Class<?>... parameterTypes) {
		try {
			return ReflectionUtil.getMethod(Class.forName(className), name, parameterTypes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		try {
			Method method = clazz.getDeclaredMethod(name, parameterTypes);
			method.setAccessible(true);
			return method;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Method getMethodByType(Class<?> clazz, Class<?> target, int index, Class<?>... arguments) {
		int currentIndex = 0;
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.getReturnType().equals(target)) {
				Class<?>[] parameters = method.getParameterTypes();
				if (parameters.length != arguments.length) {
					continue;
				}

				boolean result = true;
				for (int i = 0; i < parameters.length; i++) {
					if (!parameters[i].equals(arguments[i])) {
						result = true;
						break;
					}
				}

				if (result) {
					if (currentIndex == index) {
						method.setAccessible(true);
						return method;
					}
					currentIndex++;
				}
			}
		}

		throw new NullPointerException(String.format("Unable to find method in class %s for return type %s with index %d",
				clazz.getSimpleName(),
				target.getSimpleName(),
				index));
	}
}
