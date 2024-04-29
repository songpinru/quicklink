package org.example.inject;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.example.base.Source;

public interface InjectSourceWithKey<T> extends InjectEnvWithKey<DataStream<T>> {

}

