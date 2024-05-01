package org.example.base;

import org.apache.flink.api.common.eventtime.Watermark;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

class SinkFunctionProxy<T> implements Sink<T> {
    private final SinkFunction<T> sinkFunction;

    SinkFunctionProxy(SinkFunction<T> sinkFunction) {
        this.sinkFunction = sinkFunction;
    }

    @Override
    public void invoke(T value) throws Exception {
        sinkFunction.invoke(value);
    }

    @Override
    public void invoke(T value, Context context) throws Exception {
        sinkFunction.invoke(value, context);
    }

    @Override
    public void writeWatermark(Watermark watermark) throws Exception {
        sinkFunction.writeWatermark(watermark);
    }

    @Override
    public void finish() throws Exception {
        sinkFunction.finish();
    }
}
