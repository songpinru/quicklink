package com.prdi.di;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pinru
 * @version 1.0
 * @date 2024/5/6
 */
public class ContainerContext<T> {
    private static final Logger logger = LoggerFactory.getLogger(ContainerContext.class);

    private final Container container;
    private final Key<T> entryPoint;

    public ContainerContext(final Container container, final Key<T> entryPoint) {
        logger.info("creating container context for {}", entryPoint);
        logger.debug("container context: {}", container);
        this.container = container;
        this.entryPoint = entryPoint;
    }

    public  T getEntryPoint() {
        return getInstance(entryPoint);
    }

    private <T> T getInstance(final Key<T> key) {
        return container.get(key);
    }

    public <T> T getInstance(Class<T> clazz, String name) {
        return getInstance(Key.of(clazz, name));
    }

    public <T> T getInstance(Class<T> clazz) {
        return getInstance(clazz, "");
    }
}
