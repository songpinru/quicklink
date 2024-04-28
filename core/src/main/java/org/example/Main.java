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
                .addSource(new BeanWithKey<InjectSource<String>>("",TestSource::new))
                .addSource("key", TestSource::new)
                .addSink("sink",new DiscardingSink<String>())
                .build()
                .run(new TestJobFactory(),args);
    }
}