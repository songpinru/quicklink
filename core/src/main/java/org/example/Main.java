package org.example;

import org.apache.flink.streaming.api.functions.sink.DiscardingSink;
import org.example.base.*;
import org.example.inject.InjectSource;
import org.example.test.TestJobFactory;
import org.example.test.TestSource;

public class Main  {
    public static void main(String[] args) {
        FlinkApplication
                .builder()
                .addSource("test",context -> context.env().fromElements(1))
//                .addSource("key", TestSource::new)
                .addSink("sink",new DiscardingSink<String>())
                .addSink("",()->new DiscardingSink<>())
                .build()
                .run(new TestJobFactory(),args);
    }
}