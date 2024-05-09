package com.prdi.di;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author pinru
 * @version 1.0
 * @date 2024/5/6
 */
public class GraphContext {
    private static final Logger logger = LoggerFactory.getLogger(GraphContext.class);

    private final Map<Key, InjectFunction<?>> injectMap;
    private final Map<Key, List<Key>> dependency;
    private final Map<Key, List<Key>> dependOn;

    public GraphContext() {
        injectMap = new HashMap<>();
        dependency = new HashMap<>();
        dependOn = new HashMap<>();
    }

    void injectBean(Key key, InjectFunction<?> injectFunction, List<Key> paramKeys) {
        logger.debug("inject bean {}", key);
        injectMap.put(key, injectFunction);
        edgeRelation(key, paramKeys);
    }

    boolean isKeyExist(Key key) {
        return injectMap.containsKey(key);
    }

    public <T> ContainerContext<T> entrypoint(Class<T> clazz, String name) {
        return entrypoint(Key.of(clazz, name));
    }

    public <T> ContainerContext<T> entrypoint(Class<T> clazz) {
        return entrypoint(clazz, "");
    }

    <T> ContainerContext<T> entrypoint(Key<T> key) {
        final ConcurrentMap<Key, Integer> topologyMap = new ConcurrentHashMap<>();
        topology(topologyMap, key);

        final Container container = generateContainer(topologyMap);
        return new ContainerContext<>(container, key);
    }

    private Container generateContainer(Map<Key, Integer> topologyMap) {
        logger.info("generate container, topologyMap: {}", topologyMap);
        final Container container = new Container();
        // 把入度为0的项加入容器，并把相关的项的计数-1，直至完成
        while (!topologyMap.isEmpty()) {
            logger.debug("the rest of topologyMap: {}", topologyMap);
            logger.debug("the current container: {}", container);
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
        logger.debug("topology bean:{}", key);
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

    private void edgeRelation(Key key, List<Key> paramKeys) {
        dependency.put(key, paramKeys);
        paramKeys.forEach(p -> dependOn.computeIfAbsent(p, k -> new ArrayList<>()).add(key));
    }

}
