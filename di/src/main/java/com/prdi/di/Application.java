package com.prdi.di;

import com.prdi.di.annotation.processor.ConfigurationProvider;
import com.prdi.di.annotation.processor.ServiceProvider;
import com.prdi.di.api.Bean;
import com.prdi.di.api.Bind;
import com.prdi.di.api.Inject;
import com.prdi.di.api.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.*;

/**
 * @author pinru
 * @version 1.0
 * @date 2024/5/4
 */
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    public static String ARGS = "args";


    private final List<Class<?>> defaultServices;
    private final List<Object> defaultConfig;
    private final GraphContext context;

    public static Application app(String[] args){
        return new Application(args);
    }

    public Application(String[] args) {
        defaultServices = new ArrayList<>();
        defaultConfig = new ArrayList<>();
        context = new GraphContext();

        initArgs(args);
    }

    private void initArgs(final String[] args) {
        logger.debug("Application args: {}", Arrays.toString(args));
        final Key<String[]> argsKey = new Key<>(String[].class, ARGS);
        context.injectBean(argsKey, container -> args, List.of());
    }

    public Application addDefaultService(Class<?> clazz) {
        defaultServices.add(clazz);
        return this;
    }

    public Application addDefaultConfig(Object config) {
        defaultConfig.add(config);
        return this;
    }



    public GraphContext initialize() {
        logger.info("init application.");
        ServiceLoader
                .load(ServiceProvider.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .map(ServiceProvider::get)
                .filter(service -> !defaultServices.contains(service))
                .forEach(this::injectService);
        ServiceLoader
                .load(ConfigurationProvider.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .map(ConfigurationProvider::get)
                .filter(config -> !defaultConfig.contains(config))
                .forEach(this::injectConfiguration);
        defaultServices.forEach(this::injectDefaultService);
        defaultConfig.forEach(this::injectionDefaultConfiguration);
        return context;
    }

    private void injectConfiguration(Class<?> clazz) {
        logger.debug("inject configuration {}", clazz);
        final Object configuration;
        try {
            configuration = clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        injectionConfiguration(configuration, false);
    }

    private void injectionDefaultConfiguration(Object configuration) {
        logger.debug("inject default configuration {}", configuration);
        injectionConfiguration(configuration, true);
    }

    private void injectionConfiguration(Object configuration, boolean ignore) {
        Arrays
                .stream(configuration.getClass().getMethods())
                .filter(method -> method.getAnnotation(Bean.class) != null)
                .forEach(method -> {
                    final Class<?> bind = getBind(method);
                    final String name = getName(method);
                    final Key key = new Key(bind, name);
                    if (isKeyExist(key, ignore)) return;

                    final List<Key> paramKeys = getKeys(method);
                    final InjectFunction<?> injectFunction = container -> {
                        try {
                            return method.invoke(configuration, paramKeys
                                    .stream()
                                    .map(container::get)
                                    .toArray());
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }

                    };
                    context.injectBean(key, injectFunction, paramKeys);
                });
    }




    private void injectService(Class<?> clazz) {
        logger.debug("inject service: {}", clazz);
        injectService(clazz, false);
    }

    private void injectDefaultService(Class<?> clazz) {
        logger.debug("inject default service: {}", clazz);
        injectService(clazz, true);
    }

    private void injectService(Class<?> clazz, boolean ignore) {
        final Class<?> bind = getBind(clazz);
        final String name = getName(clazz);
        final Key key = new Key(bind, name);
        // 如果key存在，就不用再继续了
        if (isKeyExist(key, ignore)) return;

        final Constructor<?>[] constructors = clazz.getConstructors();
        Constructor<?> constructor;
        if (constructors.length == 1) {
            constructor = constructors[0];
        } else {
            final List<Constructor<?>> constructorWithInject = Arrays
                    .stream(constructors)
                    .filter(con -> con.getAnnotation(Inject.class) != null)
                    .toList();

            if (constructorWithInject.size() > 1) {
                // TODO:输出Warning
                throw new RuntimeException("构造函数冲突");
            }
            constructor = constructorWithInject.get(0);
        }
        final List<Key> paramKeys = getKeys(constructor);

        final InjectFunction<?> injectFunction = container -> {

            try {
                return constructor.newInstance(paramKeys
                        .stream()
                        .map(container::get)
                        .toArray());
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        };

        context.injectBean(key, injectFunction, paramKeys);
    }

    private boolean isKeyExist(Key key, boolean ignore) {
        if (context.isKeyExist(key)) {
            if (ignore) {
                return true;
            } else {
                throw new RuntimeException("依赖冲突");
            }
        }
        return false;
    }



    private static List<Key> getKeys(Executable executable) {
        final ArrayList<Key> paramsKey = new ArrayList<>();
        for (final Parameter parameter : executable.getParameters()) {

            final String paramName = getName(parameter);
            paramsKey.add(new Key(parameter.getType(), paramName));
        }
        return paramsKey;
    }


    private static Class<?> getBind(Class<?> clazz) {
        final Bind annotation = clazz.getAnnotation(Bind.class);
        return getBind(clazz, annotation);
    }

    private static Class<?> getBind(Method method) {
        final Bind annotation = method.getAnnotation(Bind.class);
        final Class<?> returnType = method.getReturnType();
        return getBind(returnType, annotation);
    }

    private static Class<?> getBind(Class<?> clazz, Bind annotation) {
        final Class<?> bind;
        if (clazz.isInterface()) {
            bind=clazz;
        } else {
            final Class<?>[] interfaces = clazz.getInterfaces();
             bind = interfaces.length == 1 ? interfaces[0] : clazz;
        }
        return bindOrDefault(bind, annotation);
    }


    private static Class<?> bindOrDefault(Class<?> returnType, Bind annotation) {
        return annotation == null ? returnType : annotation.value();
    }

    private static String getName(Parameter parameter) {
        final Named annotation = parameter.getAnnotation(Named.class);
        return nameOrDefault(annotation);
    }

    private static String getName(Class<?> clazz) {
        final Named annotation = clazz.getAnnotation(Named.class);
        return nameOrDefault(annotation);
    }

    private static String getName(Executable method) {
        final Named annotation = method.getAnnotation(Named.class);
        return nameOrDefault(annotation);
    }

    private static String nameOrDefault(Named annotation) {
        return annotation == null ? "" : annotation.value();
    }


}


