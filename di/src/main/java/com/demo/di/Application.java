package com.demo.di;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.demo.annotation.processor.ConfigurationProvider;
import com.demo.annotation.processor.ServiceProvider;
import com.demo.di.api.Bean;
import com.demo.di.api.Bind;
import com.demo.di.api.Inject;
import com.demo.di.api.Named;

/**
 * @author pinru
 * @version 1.0
 * @date 2024/5/4
 */
public class Application {


    public Application(String[] args) {
        final Key<String[]> argsKey = new Key<>(String[].class, ARGS);
        inject(argsKey, container -> args);
        edgeRelation(argsKey, List.of());
    }

    public static Application init(String[] args) {
        return new Application(args);
    }

    public static String ARGS = "args";

    private final List<Class<?>> defaultServices = new ArrayList<>();
    private final List<Object> defaultConfig = new ArrayList<>();

    private final Map<Key, InjectFunction<?>> injectMap = new HashMap<>();

    private final Map<Key, List<Key>> dependency = new HashMap<>();
    private final Map<Key, List<Key>> dependOn = new HashMap<>();

    public Application addDefaultService(Class<?> clazz) {
        defaultServices.add(clazz);
        return this;
    }

    public Application addDefaultConfig(Object config) {
        defaultConfig.add(config);
        return this;
    }
    public <T> T getInstance(Class<T> clazz,String name){
        return getInstance(Key.of(clazz,name));
    }
    public <T> T getInstance(Class<T> clazz){
        return getInstance(clazz,"");
    }
    private  <T> T getInstance(Key<T> key) {
        initialize();
        System.out.println("defaultServices: " + defaultServices);
        System.out.println("defaultConfig:   " + defaultConfig);
        System.out.println("dependency:      " + dependency);
        System.out.println("dependOn:        " + dependOn);
        final ConcurrentMap<Key, Integer> topologyMap = new ConcurrentHashMap<>();
        topology(topologyMap, key);

        final Container container = generateContainer(topologyMap);
        return container.get(key);
    }

    private Container generateContainer(Map<Key, Integer> topologyMap) {
        final Container container = new Container();
        // 把入度为0的项加入容器，并把相关的项的计数-1，直至完成
        while (!topologyMap.isEmpty()) {
            System.out.println("topology:       " + topologyMap);
            topologyMap.forEach((k, c) -> {
                if (c == 0) {
                    final InjectFunction<?> injectFunction = injectMap.get(k);
                    container.add(k, injectFunction.provide(container));
                    computeTopology(topologyMap, k);
                    topologyMap.remove(k);
                }
            });
        }
        return container;
    }

    private void computeTopology(Map<Key, Integer> topologyMap, Key k) {
        dependOn
                .getOrDefault(k, List.of())
                .forEach(in -> {
                    topologyMap.computeIfPresent(in, (i, v) -> v - 1);
                    computeTopology(topologyMap, in);
                });
    }

    private int topology(Map<Key, Integer> map, Key key) {
        final List<Key> dependencies = dependency.get(key);
        if (dependencies == null) {
            throw new RuntimeException("依赖不全:" + key);
        }
        int count = dependencies.size();
        for (final Key dependency : dependencies) {
            count += topology(map, dependency);
        }
        map.put(key, count);
        return count;
    }

    private int dependencyCount(Key key) {
        final List<Key> dependencies = dependency.getOrDefault(key, List.of());
        int count = dependencies.size();
        for (final Key dependency : dependencies) {
            count += dependencyCount(dependency);
        }
        return count;
    }

    private void initialize() {
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

    }

    private void injectConfiguration(Class<?> clazz) {
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
                    inject(key, injectFunction);
                    edgeRelation(key, paramKeys);
                });
    }


    private void injectService(Class<?> clazz) {
        injectService(clazz, false);
    }

    private void injectDefaultService(Class<?> clazz) {
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

        inject(key, injectFunction);
        edgeRelation(key, paramKeys);
    }

    private boolean isKeyExist(Key key, boolean ignore) {
        if (injectMap.containsKey(key)) {
            if (ignore) {
                return true;
            } else {
                throw new RuntimeException("依赖冲突");
            }
        }
        return false;
    }

    private void inject(Key key, InjectFunction<?> bean) {
        injectMap.put(key, bean);
    }

    private void edgeRelation(Key key, List<Key> paramKeys) {
        dependency.put(key, paramKeys);
        paramKeys.forEach(p -> dependOn.computeIfAbsent(p, k -> new ArrayList<>()).add(key));
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
        if (clazz.isInterface()) {
            return bindOrDefault(clazz, annotation);
        } else {
            final Class<?>[] interfaces = clazz.getInterfaces();
            final Class<?> bind = interfaces.length == 1 ? interfaces[0] : clazz;
            return bindOrDefault(bind, annotation);
        }
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


