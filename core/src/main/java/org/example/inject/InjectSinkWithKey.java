package org.example.inject;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public interface InjectSinkWithKey<T> extends InjectWithKey<SinkFunction<T>>{
}
