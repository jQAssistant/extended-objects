package com.buschmais.cdo.api.bootstrap;

import com.buschmais.cdo.api.CdoManagerFactory;

public class Cdo {

    private static final Cdo instance = new Cdo();

    private CdoUnitFactory cdoUnitFactory;

    private Cdo() {
        cdoUnitFactory = new CdoUnitFactory();
    }

    private static Cdo getInstance() {
        return instance;
    }

    public static CdoManagerFactory createCdoManagerFactory(String name) {
        CdoUnit cdoUnit;
        cdoUnit = getInstance().cdoUnitFactory.getCdoUnit(name);
        Class<? extends CdoProvider> providerType = cdoUnit.getProvider();
        CdoProvider cdoProvider = ClassHelper.newInstance(providerType);
        return cdoProvider.createCdoManagerFactory(cdoUnit);
    }
}
