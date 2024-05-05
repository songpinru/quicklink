package com.demo.core.base;


import com.demo.core.config.ConfigurationUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class FlinkApplication {


    private static final Logger logger = LoggerFactory.getLogger(FlinkApplication.class);





    public static void run(Class<?> job,String[] args) {

        // 解析注解
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


        // 环境准备
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

        // 执行流程
//        logger.debug("The task {} is ready to run.", jobName);

//        HashMap<String, Source<?>> sourceMap = new HashMap<>();



//        context.env=env;
        // catch一下运行时错误，打印日志
        try {
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


}
