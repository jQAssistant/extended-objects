package com.buschmais.cdo.api.test.bootstrap.provider;

import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.bootstrap.CdoProvider;
import com.buschmais.cdo.api.bootstrap.CdoUnit;

public class TestCdoProvider implements CdoProvider {

    @Override
    public CdoManagerFactory createCdoManagerFactory(CdoUnit cdoUnit) {
        return new TestCdoManagerFactory(cdoUnit);
    }

}
