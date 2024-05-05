package com.demo.di.api;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 在@Service标注的类里，通过构造器注入这个类的对象
 * @author pinru
 * @version 1.0
 * @date 2024/5/4
 */
@Target({CONSTRUCTOR})
@Retention(RUNTIME)
@Documented
public @interface Inject {
}
