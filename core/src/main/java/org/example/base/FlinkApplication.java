package org.example.base;


import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.example.config.ConfigurationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;


public final class FlinkApplication {
    private static final Logger logger = LoggerFactory.getLogger(FlinkApplication.class);

    public static void run(Class<? extends FlinkJob> primaryService, String... args) {
        if (primaryService == null) {
            logger.error("The primaryService is null.");
            throw new RuntimeException("primaryService must not be null");
        }

        //解析注解
        String jobName;
        if (primaryService.isAnnotationPresent(JobName.class)) {
            JobName jobNameAnnotation = primaryService.getAnnotation(JobName.class);
            jobName = jobNameAnnotation.value();
        } else {
            jobName = primaryService.getSimpleName();
        }
        if (jobName == null) {
            jobName = primaryService.getSimpleName();
        }
        logger.info("The job {} starts running.", jobName);


        //环境准备
//        ParameterTool fromArgs = ParameterTool.fromArgs(args);
//        logger.info("Handle args: {}", fromArgs.getConfiguration());
//        logger.debug("Starts loading the configuration file.");
//        ParameterTool configurations = ConfigurationUtil.loadConfigurations(fromArgs);
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//
//        env.getConfig().setGlobalJobParameters(configurations);


        //实例化，自动装配
        FlinkJob flinkJob = null;
        try {
            flinkJob = primaryService.getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            logger.error("Unable to instance Class: {}, may be an interface or abstract class,.", primaryService.getName(),e);
            throw new RuntimeException("instance failed", e);
        } catch (InvocationTargetException e) {
            logger.error("Unable to instance Class: {}, constructor throw an exception .", primaryService.getName(),e);
            throw new RuntimeException("instance failed", e);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            logger.error("Unable to instance, there must be an public constructor which parameters is empty.",e);
            throw new RuntimeException("access constructor failed", e);
        }

        //执行流程
        logger.debug("The task {} is ready to run.", jobName);

        // catch一下运行时错误，打印日志
        try {
            flinkJob.process();
        } catch (Exception e) {
            logger.error("The task throw an unexpected Exception.");
            throw new RuntimeException(e);
        }

        logger.debug("The job {} is ready to execute.", jobName);
        try {
//            env.execute(jobName);
        } catch (Exception e) {
            logger.error("The job executed failed.");
            throw new RuntimeException(e);
        }
    }
}
