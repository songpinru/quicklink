package com.prdi.di.annotation.processor;

import java.lang.annotation.*;

/**
 * 类似Spring的Configuration，用于通过在方法上使用@Bean注册Bean
 * @author pinru
 * @version 1.0
 * @date 2024/5/4
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface Configuration {
}
