package com.prdi.di.annotation.processor;

import java.lang.annotation.*;

/**
 * 类似Spring的Service，注入被@Service注释的类的对象，只能在类上使用
 * @author pinru
 * @version 1.0
 * @date 2024/5/3
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface Service {
}
