package com.prdi.di.api;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 在@Configuration标注的类里，注入@Bean标注的方法返回的对象
 * @author pinru
 * @version 1.0
 * @date 2024/5/4
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface Bean {
}
