package org.example.base;

public abstract class SourceBase<T> implements Source<T> {
    protected EnvContext context;

    public SourceBase(EnvContext envContext) {
        this.context = envContext;
    }
}
