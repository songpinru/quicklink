package org.example.test;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.example.base.EnvContext;
import org.example.base.SourceBase;

public class TestSource extends SourceBase<String> {
    public static String ID="";

    public TestSource(EnvContext envContext) {
        super(envContext);
    }

    @Override
    public DataStream<String> createStream() {
        return context.env.fromElements("1");
    }
}
