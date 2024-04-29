package org.example.inject;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.example.base.BeanWithKey;
import org.example.base.ContainerContext;
import org.example.base.EnvContext;
import org.example.base.Source;

public interface InjectSourceWithKey<T> extends InjectWithKey<DataStream<T>> {
    BeanWithKey<DataStream<T>> inject(EnvContext context);

    @Override
    default BeanWithKey<DataStream<T>> inject(ContainerContext context){
        return inject((EnvContext)context);
    };
}

