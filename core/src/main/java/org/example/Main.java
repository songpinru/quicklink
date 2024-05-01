package org.example;

import org.apache.flink.streaming.api.functions.sink.DiscardingSink;
import org.example.base.FlinkApplication;
import org.example.inject.InjectDataStream;
import org.example.test.TestJobFactory;

public class Main {

    public static void main(String[] args) {
        InjectDataStream<Integer> source = context -> context.env().fromElements(1);
        FlinkApplication.builder().addSource("test", source)
//                .addSource("key", TestSource::new)
//                .addSink("sink",new DiscardingSink<String>())
                .addSink("", () -> new DiscardingSink<>()).build()
//                .run(context -> {},args);
                .run(new TestJobFactory(), args);
    }
}