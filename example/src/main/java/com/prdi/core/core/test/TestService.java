package com.prdi.core.core.test;

import com.prdi.core.core.base.Source;
import com.prdi.di.annotation.processor.Service;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

/**
 * @author pinru
 * @version 1.0
 * @date 2024/5/5
 */
@Service
public class TestService {
    final Source<Long> source;
    final SinkFunction<String> sinkFunction;

    public TestService(Source<Long> source, SinkFunction<String> sinkFunction) {
        this.source = source;
        this.sinkFunction = sinkFunction;
    }

    public void run(){
        source.createStream().map(String::valueOf).addSink(sinkFunction);
    }
}
