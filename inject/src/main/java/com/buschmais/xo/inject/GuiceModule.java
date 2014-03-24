package com.buschmais.xo.inject;

import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Singleton;

public class GuiceModule extends AbstractModule {

    private final String defaultUnit;

    public GuiceModule() {
        this("default");
    }

    public GuiceModule(String defaultUnit) {
        this.defaultUnit = defaultUnit;
    }

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    XOManagerFactory cdoManagerFactory() {
        return XO.createXOManagerFactory(defaultUnit);
    }
}
