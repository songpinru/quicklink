package com.prdi.di;

/**
 * @author pinru
 * @version 1.0
 * @date 2024/5/4
 */
interface InjectFunction<T> {
    T provide(Container container);
}
