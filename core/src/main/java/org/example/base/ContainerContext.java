package org.example.base;

import java.util.Map;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public class ContainerContext {

    private final Map<String, ?> beanMap;

    public ContainerContext(Map<String, ?> beanMap) {
        this.beanMap = beanMap;
    }


    public <T> Source<T> getSource(String key) {
        return (Source<T>) beanMap.get(key);
    }

    public <T> SinkFunction<T> getSink(String key) {
        return (SinkFunction<T>) beanMap.get(key);
    }

    public <T> T getBean(String key) {
        return (T) beanMap.get(key);
    }

}
