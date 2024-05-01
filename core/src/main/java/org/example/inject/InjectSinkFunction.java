package org.example.inject;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public interface InjectSinkFunction<T> extends InjectBean<SinkFunction<T>> {
}
