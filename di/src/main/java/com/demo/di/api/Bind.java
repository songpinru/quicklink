package com.demo.di.api;

import java.lang.annotation.*;

/**
 * @author pinru
 * @version 1.0
 * @date 2024/5/4
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bind {
    Class<?> value();
}
