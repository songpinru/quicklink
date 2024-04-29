package org.example.base;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Map;

public class EnvContext extends ContainerContext {
    private StreamExecutionEnvironment env;

    public EnvContext(Map<String, ?> beanMap, StreamExecutionEnvironment env) {
        super(beanMap);
        this.env = env;
    }

    public StreamExecutionEnvironment env() {
        return env;
    }


}
