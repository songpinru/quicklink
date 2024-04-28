package org.example.inject;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public interface InjectSink<T> extends InjectBean<SinkFunction<T>> {
}
