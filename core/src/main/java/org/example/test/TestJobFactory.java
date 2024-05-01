package org.example.test;

import org.apache.flink.streaming.api.functions.sink.DiscardingSink;
import org.example.base.ContainerContext;
import org.example.base.JobFactory;

public class TestJobFactory implements JobFactory<TestApplication> {
    @Override
    public TestApplication create(ContainerContext context) {
        TestApplication testApplication = new TestApplication();
//        System.out.println(context.getSource("test"));
        testApplication.source = context.getSource("test");
        testApplication.sink = new DiscardingSink<>();
        return testApplication;
    }
}
