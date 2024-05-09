package com.prdi.core.core.base;

import org.apache.flink.streaming.api.datastream.DataStream;

public interface Source<T> {
    DataStream<T> createStream();
}
