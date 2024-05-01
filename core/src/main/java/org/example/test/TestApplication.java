package org.example.test;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.example.Main;
import org.example.base.FlinkJob;
import org.example.base.Source;
import org.example.util.ClassUtils;

public class TestApplication implements FlinkJob {
    // 注入Source等
    Source<String> source;
    SinkFunction<String> sink;

    @Override
    public void process() throws Exception {
        ClassUtils.getClasses(Main.class.getPackage().getName()).stream().forEach(System.out::println);
        source.createStream().addSink(sink);
    }


}
