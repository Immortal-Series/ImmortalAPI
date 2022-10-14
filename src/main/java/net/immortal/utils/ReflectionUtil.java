package net.immortal.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReflectionUtil {
    private static final Logger log = Logger.getLogger(ReflectionUtil.class.getName());

    public static String getCraftBukkitVersion() {
        return cb().split("\\.")[3];
    }

    public static String getNMSVersion(Object nmsObject) {
        return nmsObject != null ? nmsObject.getClass().getPackage().getName().split("\\.")[3] : "";
    }

    public static String getNMSVersion() {
        return nms().split("\\.")[3];
    }

    public static String nms() {
        Object nmsServer = exec(Bukkit.getServer(), "getServer");
        return nmsServer != null ? nmsServer.getClass().getPackage().getName() : "net.minecraft.server";
    }

    public static String cb() {
        return Bukkit.getServer().getClass().getPackage().getName();
    }

    public static String getPackageName(Object nmsObject) {
        return nmsObject != null ? nmsObject.getClass().getPackage().getName() : "";
    }

    public static Class<?> getBukkitClass(Object craftObject) {
        Class clazz = craftObject != null ? craftObject.getClass() : null;
        while (clazz != null && clazz.getCanonicalName().contains(".craftbukkit.")) {
            clazz = clazz.getSuperclass();
        }
        return clazz;
    }

    public static <T> T execStatic(Class<?> clazz, String methodName, Object... args) {
        try {
            Class[] argTypes = new Class[args.length];
            int ix = 0;
            for (Object arg : args) {
                argTypes[ix++] = getBukkitClass(arg);
            }
            Method method = getMethod(clazz, methodName, argTypes);
            boolean wasAccessible = method.isAccessible();
            method.setAccessible(true);
            try {
                return (T) method.invoke(null, args);
            } finally {
                method.setAccessible(wasAccessible);
            }
        } catch (NoSuchMethodException e) {
            log.fine("Unable to locate method " + methodName + " on " + clazz);
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.log(Level.INFO, "Calling " + methodName + " on " + clazz + " threw an exception", e);
        }
        return null;
    }

    public static <T> T exec(Object obj, String methodName, Class[] argTypes, Object... args) {
        if (obj == null) {
            return null;
        }
        Class<?> aClass = obj.getClass();
        try {
            Method method = getMethod(aClass, methodName, argTypes);
            boolean wasAccessible = method.isAccessible();
            method.setAccessible(true);
            try {
                return (T) method.invoke(obj, args);
            } finally {
                method.setAccessible(wasAccessible);
            }
        } catch (NoSuchMethodException | AbstractMethodError e) {
            log.fine("Unable to locate method " + methodName + "(" + Arrays.asList(argTypes) + ") on " + aClass);
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.log(Level.INFO, "Calling " + methodName + " on " + obj + " threw an exception", e);
        }
        return null;
    }

    private static Method getMethod(Class<?> aClass, String methodName, Class[] argTypes) throws NoSuchMethodException {
        try {
            return aClass.getDeclaredMethod(methodName, argTypes);
        } catch (NoSuchMethodException e) {
            return aClass.getMethod(methodName, argTypes);
        }
    }

    public static <T> T exec(Object obj, String methodName, Object... args) {
        if (obj == null) {
            return null;
        }
        Class[] argTypes = new Class[args.length];
        int ix = 0;
        for (Object arg : args) {
            argTypes[ix++] = arg != null ? arg.getClass() : null;
        }
        return exec(obj, methodName, argTypes, args);
    }

    public static <T> T getField(Object obj, String fieldName) {
        try {
            Field field = getFieldInternal(obj, fieldName);
            boolean wasAccessible = field.isAccessible();
            field.setAccessible(true);
            try {
                return (T) field.get(obj);
            } finally {
                field.setAccessible(wasAccessible);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.fine("Unable to find field " + fieldName + " on " + obj);
        }
        return null;
    }

    private static Field getFieldInternal(Object obj, String fieldName) throws NoSuchFieldException {
        return getFieldFromClass(obj.getClass(), fieldName);
    }

    private static Field getFieldFromClass(Class<?> aClass, String fieldName) throws NoSuchFieldException {
        if (aClass == null) {
            throw new NoSuchFieldException("Unable to locate field " + fieldName);
        }
        try {
            return aClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException ignored) { }
        try {
            return aClass.getField(fieldName);
        } catch (NoSuchFieldException ignored) { }
        return getFieldFromClass(aClass.getSuperclass(), fieldName);
    }

    public static <T> void setField(Object obj, String fieldName, T field) {
        try {
            Field declaredField = getFieldInternal(obj, fieldName);
            boolean wasAccessible = declaredField.isAccessible();
            declaredField.setAccessible(true);
            try {
                declaredField.set(obj, field);
            } finally {
                declaredField.setAccessible(wasAccessible);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.fine("Unable to find field " + fieldName + " on " + obj);
        }
    }

    public static <T> T newInstance(String className, Class<?>[] argTypes, Object... args) {
        try {
            Class<?> aClass = Class.forName(className);
            Constructor<?> constructor = aClass.getDeclaredConstructor(argTypes);
            return (T) constructor.newInstance(args);
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            log.fine("Unable to instantiate object of type " + className + ":" + e);
        }
        return null;
    }

    public static <T> T newInstance(String className, Object... args) {
        Class[] argTypes = new Class[args.length];
        int ix = 0;
        for (Object arg : args) {
            argTypes[ix++] = arg != null ? arg.getClass() : null;
        }
        return newInstance(className, argTypes, args);
    }
}
