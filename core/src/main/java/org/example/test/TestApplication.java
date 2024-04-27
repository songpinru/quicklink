package org.example.test;

import org.example.Main;
import org.example.base.FlinkApplication;
import org.example.base.FlinkApplicationBootstrap;
import org.example.base.FlinkJob;
import org.example.util.ClassUtil;
import org.example.util.ClassUtils;

import java.util.Arrays;
import java.util.ServiceLoader;
import java.util.concurrent.Flow;
import java.util.function.Consumer;

public class TestApplication implements FlinkJob {
    //注入Source等

    @Override
    public void process() throws Exception {
         ClassUtils.getClasses(Main.class.getPackage().getName()).stream().forEach(System.out::println );

    }

    class AA{
        int a;
    }


}
