package org.example.inject;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.example.base.Source;

public interface InjectSource<T> extends InjectEnv<DataStream<T>> {
}
