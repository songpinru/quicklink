package org.example.inject;

import org.example.base.ContainerContext;

public interface InjectBean<T> {
    T inject(ContainerContext context);
}
