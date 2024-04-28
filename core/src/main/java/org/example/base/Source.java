package org.example.base;

import org.apache.flink.streaming.api.datastream.DataStream;

public interface Source<T> {
    DataStream<T> createStream();
}
