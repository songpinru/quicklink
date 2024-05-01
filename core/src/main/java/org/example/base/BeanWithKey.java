package org.example.base;

public class BeanWithKey<T> {
    private String key;
    private T bean;

    public BeanWithKey(String key, T bean) {
        this.key = key;
        this.bean = bean;
    }

    public static <I> BeanWithKey<I> from(String key, I bean) {
        return new BeanWithKey<>(key, bean);
    }

    public String getKey() {
        return key;
    }

    public T getBean() {
        return bean;
    }

    ;

}
