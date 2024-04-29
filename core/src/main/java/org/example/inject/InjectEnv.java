package org.example.inject;

import org.example.base.ContainerContext;
import org.example.base.EnvContext;

interface InjectEnv<T> extends InjectBean<T>{
    @Override
    T inject(EnvContext context);
}
