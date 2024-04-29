package org.example.inject;

import org.example.base.ContainerContext;
import org.example.base.EnvContext;

interface InjectEnv<T> extends InjectBean<T>{
    T inject(EnvContext context);

    @Override
    default T inject(ContainerContext context){
        return inject((EnvContext)context);
    };
}
