package org.example.base;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public interface Sink<T> extends SinkFunction<T> {
}
