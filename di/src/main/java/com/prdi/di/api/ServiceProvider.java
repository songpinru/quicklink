package com.prdi.di.api;


/**
 * 辅助@Service的接口
 * @author pinru
 * @version 1.0
 * @date 2024/5/4
 */
public interface ServiceProvider extends Provider<Class<?>>{
    Class<?> get();
}
