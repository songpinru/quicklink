package org.example.inject;

import org.example.base.EnvContext;

interface InjectEnv<T> {
    T inject(EnvContext context);
}
