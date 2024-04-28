package org.example.base;


import org.apache.flink.api.java.utils.ParameterTool;
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


public final class FlinkApplication {


    private static final Logger logger = LoggerFactory.getLogger(FlinkApplication.class);

    private List<InjectSourceWithKey<?>> sources;
    private List<InjectSinkWithKey<?>> sinks;
    private List<InjectWithKey<?>> beans;


    public FlinkApplication(List<InjectSourceWithKey<?>> sources, List<InjectSinkWithKey<?>> sinks, List<InjectWithKey<?>> beans) {
        this.sources = sources;
        this.sinks = sinks;
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

        HashMap<String, Source<?>> sourceMap = new HashMap<>();
        HashMap<String, SinkFunction<?>> sinkMap = new HashMap<>();
        HashMap<String, Object> beanMap = new HashMap<>();
        EnvContext envContext = new EnvContext();
        envContext.env = env;
        sources.forEach(s -> {
            BeanWithKey<? extends Source<?>> source = s.inject(envContext);
            sourceMap.put(source.getKey(), source.getBean());
        });
        sinks.forEach(s -> {
            BeanWithKey<? extends SinkFunction<?>> sink = s.inject();
            sinkMap.put(sink.getKey(), sink.getBean());
        });
        beans.forEach(b -> {
            BeanWithKey<?> bean = b.inject();
            beanMap.put(bean.getKey(), bean.getBean());
        });

        ContainerContext context = new ContainerContext(sourceMap, sinkMap, beanMap);

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
        private List<InjectSinkWithKey<?>> sinks = new ArrayList<>();
        private List<InjectWithKey<?>> beans = new ArrayList<>();


        private <T> ApplicationBuilder addSource(InjectSourceWithKey<T> injectSourceWithKey) {
            sources.add(injectSourceWithKey);
            return this;
        }

        public <T> ApplicationBuilder addSource(String key, InjectSource<T> injectBase) {
            InjectSourceWithKey<T> injectSourceWithKey = context -> {
                Source<T> source = injectBase.inject(context);
                return new BeanWithKey<>(key, source);
            };
            return addSource(injectSourceWithKey);

        }

        public <T> ApplicationBuilder addSource(String key, Class<? extends SourceBase<T>> clazz) {
            InjectSourceWithKey<T> injectSourceWithKey = context -> {
                SourceBase<T> base = null;
                try {
                    base = clazz.getConstructor(EnvContext.class).newInstance(context);
                    //todo:错误处理
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                return new BeanWithKey<>(key, base);
            };
            return addSource(injectSourceWithKey);

        }

        public <T> ApplicationBuilder addSource(BeanWithKey<InjectSource<T>> source) {
            return addSource(source.getKey(), source.getBean());
        }

        private <T> ApplicationBuilder addSink(InjectSinkWithKey<T> injectSinkWithKey) {
            sinks.add(injectSinkWithKey);
            return this;
        }

        public <T> ApplicationBuilder addSink(String key, InjectSink<T> injectBase) {
            InjectSinkWithKey<T> injectSinkWithKey = () -> new BeanWithKey<>(key, injectBase.inject());
            return addSink(injectSinkWithKey);
        }

        public <T> ApplicationBuilder addSink(String key, SinkFunction<T> sinkFunction) {
            InjectSinkWithKey<T> injectSinkWithKey = () -> new BeanWithKey<>(key, sinkFunction);
            return addSink(injectSinkWithKey);
        }

        public <T> ApplicationBuilder addSink(BeanWithKey<SinkFunction<T>> sinkFunction) {
            return addSink(sinkFunction.getKey(), sinkFunction.getBean());
        }

        private <T> ApplicationBuilder addBean(InjectWithKey<T> injectBean) {
            beans.add(injectBean);
            return this;
        }

        public <T> ApplicationBuilder addBean(String key, InjectBean<T> injectBean) {
            InjectWithKey<T> injectBeanWithKey = () -> new BeanWithKey<>(key, injectBean.inject());
            return addBean(injectBeanWithKey);
        }

        public <T> ApplicationBuilder addBean(String key, T injectBean) {
            InjectWithKey<T> injectBeanWithKey = () -> new BeanWithKey<>(key, injectBean);
            return addBean(injectBeanWithKey);
        }

        public FlinkApplication build() {
            return new FlinkApplication(sources, sinks, beans);
        }


    }

    public static ApplicationBuilder builder() {
        return new ApplicationBuilder();
    }
}
