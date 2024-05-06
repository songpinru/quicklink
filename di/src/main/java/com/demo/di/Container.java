package com.demo.di;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pinru
 * @version 1.0
 * @date 2024/5/4
 */
public class Container {
    private final Map<Key, Object> beans=new HashMap<>();

    public <T> T get(Key key) {
        return (T) beans.get(key);
    }

    public <T> void add(Key key, T value) {
        beans.put(key, value);
    }

    @Override
    public String toString() {
        return beans.toString();
    }
}
