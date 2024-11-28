package com.payby.pos.common.helper;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public final class Singleton {

    private final Map<Class, Object> INSTANCE_MAP = new HashMap<>();

    private Singleton() {

    }

    private static class SingletonHolder {
        private static final Singleton INSTANCE = new Singleton();
    }

    public static <T> T getObjectInstance(Class<T> clazz) {
        return SingletonHolder.INSTANCE.getObjectInstanceInner(clazz, null);
    }

    public static <T> T getObjectInstance(Class<T> clazz, Class[] paramTypes, Object... params) {
        return SingletonHolder.INSTANCE.getObjectInstanceInner(clazz, paramTypes, params);
    }

    private <T> T getObjectInstanceInner(Class<T> clazz, Class[] paramTypes, Object... params) {
        boolean contains = INSTANCE_MAP.containsKey(clazz);
        if (contains) {
            return (T) INSTANCE_MAP.get(clazz);
        }
        synchronized (INSTANCE_MAP) {
            contains = INSTANCE_MAP.containsKey(clazz);
            if (contains) {
                return (T) INSTANCE_MAP.get(clazz);
            }
            try {
                Constructor<T> constructor = clazz.getDeclaredConstructor(paramTypes);
                constructor.setAccessible(true);
                T instance = constructor.newInstance(params);
                INSTANCE_MAP.put(clazz, instance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (T) INSTANCE_MAP.get(clazz);
    }

}