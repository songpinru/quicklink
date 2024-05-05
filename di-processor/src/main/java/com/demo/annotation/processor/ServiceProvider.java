package com.demo.annotation.processor;

/**
 * 辅助@Service的接口
 * @author pinru
 * @version 1.0
 * @date 2024/5/4
 */
public interface ServiceProvider {
    Class<?> get();
}
