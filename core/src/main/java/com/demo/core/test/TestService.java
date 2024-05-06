package com.demo.core.test;

import com.demo.annotation.processor.Service;
import com.demo.core.base.Source;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
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
        System.out.println("1111111111");
        source.createStream().map(String::valueOf).addSink(sinkFunction);
    }
}
