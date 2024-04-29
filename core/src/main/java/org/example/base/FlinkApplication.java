package org.example.base;


import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.example.config.ConfigurationUtil;
import org.example.inject.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;


public final class FlinkApplication {


    private static final Logger logger = LoggerFactory.getLogger(FlinkApplication.class);

    private List<InjectSourceWithKey<?>> sources;
    //    private List<InjectSinkWithKey<?>> sinks;
    private List<InjectWithKey<?>> beans;


    public FlinkApplication(List<InjectSourceWithKey<?>> sources, List<InjectWithKey<?>> beans) {
        this.sources = sources;
//        this.sinks = sinks;
        this.beans = beans;
    }

    public <T extends FlinkJob> void run(JobFactory<T> primaryService, String... args) {
        if (primaryService == null) {
            logger.error("The primaryService is null.");
            throw new RuntimeException("primaryService must not be null");
        }

        //解析注解
//        String jobName;
//        if (primaryService.isAnnotationPresent(JobName.class)) {
//            JobName jobNameAnnotation = primaryService.getAnnotation(JobName.class);
//            jobName = jobNameAnnotation.value();
//        } else {
//            jobName = primaryService.getSimpleName();
//        }
//        if (jobName == null) {
//            jobName = primaryService.getSimpleName();
//        }
//        logger.info("The job {} starts running.", jobName);


        //环境准备
        ParameterTool fromArgs = ParameterTool.fromArgs(args);
        logger.info("Handle args: {}", fromArgs.getConfiguration());
        logger.debug("Starts loading the configuration file.");
        ParameterTool configurations = ConfigurationUtil.loadConfigurations(fromArgs);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//
        env.getConfig().setGlobalJobParameters(configurations);


//        //实例化，自动装配
//        JobFactory<T> flinkJob = primaryService.create();
//        try {
//            flinkJob = primaryService.getConstructor().newInstance();
//        } catch (InstantiationException e) {
//            logger.error("Unable to instance Class: {}, may be an interface or abstract class,.", primaryService.getName(), e);
//            throw new RuntimeException("instance failed", e);
//        } catch (InvocationTargetException e) {
//            logger.error("Unable to instance Class: {}, constructor throw an exception .", primaryService.getName(), e);
//            throw new RuntimeException("instance failed", e);
//        } catch (IllegalAccessException | NoSuchMethodException e) {
//            logger.error("Unable to instance, there must be an public constructor which parameters is empty.", e);
//            throw new RuntimeException("access constructor failed", e);
//        }

        //执行流程
//        logger.debug("The task {} is ready to run.", jobName);

//        HashMap<String, Source<?>> sourceMap = new HashMap<>();
        HashMap<String, Object> beanMap = new HashMap<>();
//        ContainerContext context = new ContainerContext(sourceMap,  beanMap);
        EnvContext context = new EnvContext(beanMap, env);
        sources.forEach(s -> {
            BeanWithKey<? extends DataStream<?>> source = s.inject(context);
            beanMap.put(source.getKey(),(Source)()-> source.getBean());
        });

        beans.forEach(b -> {
            BeanWithKey<?> bean = b.inject(context);
            beanMap.put(bean.getKey(), bean.getBean());
        });


//        context.env=env;
        // catch一下运行时错误，打印日志
        try {
            primaryService.create(context).process();
        } catch (Exception e) {
            logger.error("The task throw an unexpected Exception.");
            throw new RuntimeException(e);
        }

//        logger.debug("The job {} is ready to execute.", jobName);
        try {
//            env.execute(jobName);
        } catch (Exception e) {
            logger.error("The job executed failed.");
            throw new RuntimeException(e);
        }
    }

    public static class ApplicationBuilder {
        private ApplicationBuilder() {
        }

        private List<InjectSourceWithKey<?>> sources = new ArrayList<>();
        //        private List<InjectSinkWithKey<?>> sinks = new ArrayList<>();
        private List<InjectWithKey<?>> beans = new ArrayList<>();


        private <T> ApplicationBuilder addSource(InjectSourceWithKey<T> injectSourceWithKey) {
            sources.add(injectSourceWithKey);
            return this;
        }

        public <T> ApplicationBuilder addSource(String key, InjectSource<T> injectSource) {
            InjectSourceWithKey<T> injectSourceWithKey = context -> new BeanWithKey<>(key,injectSource.inject(context));

            return addSource(injectSourceWithKey);

        }
        public <T> ApplicationBuilder addSource(String key, Source<T> source) {
            return addSource(key, context -> source.createStream());
        }

//        public <T> ApplicationBuilder addSource(String key, Class<? extends SourceBase<T>> clazz) {
//            InjectSourceWithKey<T> injectSourceWithKey = context -> {
//                SourceBase<T> base = null;
//                try {
//                    base = clazz.getConstructor(EnvContext.class).newInstance(context);
//                    //todo:错误处理
//                } catch (InstantiationException e) {
//                    throw new RuntimeException(e);
//                } catch (IllegalAccessException e) {
//                    throw new RuntimeException(e);
//                } catch (InvocationTargetException e) {
//                    throw new RuntimeException(e);
//                } catch (NoSuchMethodException e) {
//                    throw new RuntimeException(e);
//                }
//                return new BeanWithKey<>(key, base);
//            };
//            return addSource(injectSourceWithKey);
//
//        }


        private <T> ApplicationBuilder addSink(InjectSinkWithKey<T> injectSinkWithKey) {
            beans.add(injectSinkWithKey);
            return this;
        }

        public <T> ApplicationBuilder addSink(String key, InjectSink<T> sink) {
            InjectSinkWithKey<T> injectSinkWithKey = context -> new BeanWithKey<>(key, sink.inject(context));
            return addSink(injectSinkWithKey);
        }

        public <T> ApplicationBuilder addSink(String key, SinkFunction<T> sinkFunction) {
            InjectSinkWithKey<T> injectSinkWithKey = context -> new BeanWithKey<>(key, sinkFunction);
            return addSink(injectSinkWithKey);
        }

        public <T> ApplicationBuilder addSink(String key, Supplier<SinkFunction<T>> sink) {
            InjectSinkWithKey<T> injectSinkWithKey = context -> new BeanWithKey<>(key, sink.get());
            return addSink(injectSinkWithKey);
        }


        private <T> ApplicationBuilder addBean(InjectWithKey<T> injectBean) {
            beans.add(injectBean);
            return this;
        }

        public <T> ApplicationBuilder addBean(String key, InjectBean<T> injectBean) {
            InjectWithKey<T> injectBeanWithKey = context -> new BeanWithKey<>(key, injectBean.inject(context));
            return addBean(injectBeanWithKey);
        }

        public <T> ApplicationBuilder addBean(String key, T bean) {
            InjectWithKey<T> injectBeanWithKey = context -> new BeanWithKey<>(key, bean);
            return addBean(injectBeanWithKey);
        }

        public FlinkApplication build() {
            return new FlinkApplication(sources, beans);
        }



    }

    public static ApplicationBuilder builder() {
        return new ApplicationBuilder();
    }
}
