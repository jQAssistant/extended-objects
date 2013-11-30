package com.buschmais.cdo.api.test.bootstrap.provider;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.bootstrap.CdoUnit;

public class TestCdoManagerFactory implements CdoManagerFactory {

    private CdoUnit cdoUnit;

    public TestCdoManagerFactory(CdoUnit cdoUnit) {
        this.cdoUnit = cdoUnit;
    }

    @Override
    public CdoManager createCdoManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
    }

    public CdoUnit getCdoUnit() {
        return cdoUnit;
    }

}
