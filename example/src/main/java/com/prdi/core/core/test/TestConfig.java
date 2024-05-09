package com.prdi.core.core.test;

import com.prdi.core.core.base.Source;
import com.prdi.di.annotation.processor.Configuration;
import com.prdi.di.api.Bean;
import com.prdi.di.api.Named;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.connector.source.lib.NumberSequenceSource;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

/**
 * @author pinru
 * @version 1.0
 * @date 2024/5/5
 */
@Configuration
public class TestConfig {
    @Bean
    public StreamExecutionEnvironment environment(){
        return StreamExecutionEnvironment.getExecutionEnvironment();
    }

    @Bean
    public SinkFunction<String> sink(){
        return new PrintSinkFunction<>();
    }

    @Bean
    public Source<Long> source(StreamExecutionEnvironment environment, org.apache.flink.api.connector.source.Source<Long,?,?> source){
        return ()->environment.fromSource(source, WatermarkStrategy.noWatermarks(),"default");
    }
    @Bean
    public ParameterTool tool(@Named("args") String[] args){
        return ParameterTool.fromArgs(args);
    }
    @Bean
    public org.apache.flink.api.connector.source.Source<Long,?,?>  get(){
        return new NumberSequenceSource(0,10);
    }

}
