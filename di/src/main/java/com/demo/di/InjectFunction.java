package com.demo.di;

/**
 * @author pinru
 * @version 1.0
 * @date 2024/5/4
 */
public interface InjectFunction<T> {
    T provide(Container container);
}
