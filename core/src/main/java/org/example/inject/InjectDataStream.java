package org.example.inject;

import org.apache.flink.streaming.api.datastream.DataStream;

public interface InjectDataStream<T> extends InjectEnv<DataStream<T>> {
}
