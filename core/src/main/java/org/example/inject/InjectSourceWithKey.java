package org.example.inject;

import org.example.base.BeanWithKey;
import org.example.base.ContainerContext;
import org.example.base.EnvContext;
import org.example.base.Source;


public interface InjectSourceWithKey<T> extends InjectWithKey<Source<T>> {
    BeanWithKey<Source<T>> inject(EnvContext context);

    @Override
    default BeanWithKey<Source<T>> inject(ContainerContext context) {
        return inject((EnvContext) context);
    }

    ;
}

