package com.demo.core.test;

import com.demo.annotation.processor.Configuration;
import com.demo.di.api.Bean;
import com.demo.di.api.Named;
import org.apache.flink.api.java.utils.ParameterTool;

/**
 * @author pinru
 * @version 1.0
 * @date 2024/5/5
 */
@Configuration
public class TestConfig {
    // @Bean
    // public StreamExecutionEnvironment environment(){
    //     return StreamExecutionEnvironment.getExecutionEnvironment();
    // }
    //
    // @Bean
    // public SinkFunction<String> sink(){
    //     return new DiscardingSink<>();
    // }
    //
    // @Bean
    // public Source<Long> source(StreamExecutionEnvironment environment, org.apache.flink.api.connector.source.Source<Long,?,?> source){
    //     return ()->environment.fromSource(source, WatermarkStrategy.noWatermarks(),"default");
    // }
    @Bean
    public ParameterTool tool(@Named("args") String[] args){
        return ParameterTool.fromArgs(args);
    }
    // @Bean
    // public org.apache.flink.api.connector.source.Source<Long,?,?>  get(){
    //     return new NumberSequenceSource(0,10);
    // }

}
