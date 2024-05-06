package com.demo.di;

/**
 * @author pinru
 * @version 1.0
 * @date 2024/5/4
 */
public record Key<T>(Class<T> clazz, String name) {
    public static <T> Key<T> of(Class<T> clazz) {
        return new Key<>(clazz, "");
    }

    public static <T> Key<T> of(Class<T> clazz, String name) {
        return new Key<>(clazz, null == name ? "" : name);
    }

    @Override
    public String toString() {
        return clazz.getSimpleName() + (name.isEmpty() ? "" : ":" + name);
    }
}
