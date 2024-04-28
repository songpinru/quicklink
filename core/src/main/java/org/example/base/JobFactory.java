package org.example.base;

public interface JobFactory<T extends FlinkJob> {
    T create(ContainerContext context);
}
