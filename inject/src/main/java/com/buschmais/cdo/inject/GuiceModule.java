package com.buschmais.cdo.inject;

import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.bootstrap.Cdo;
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
    }

    @Provides CdoManagerFactory cdoManagerFactory() {
        return Cdo.createCdoManagerFactory(defaultUnit);
    }
}
