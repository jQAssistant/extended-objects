package com.buschmais.xo.inject;

import javax.inject.Singleton;

import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

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
        // forced by interface
    }

    @Provides
    @Singleton
    XOManagerFactory<?, ?, ?, ?> xoManagerFactory() {
        return XO.createXOManagerFactory(defaultUnit);
    }
}
