package com.buschmais.cdo.impl.bootstrap;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.ValidationMode;
import com.buschmais.cdo.api.bootstrap.CdoBootstrapService;
import com.buschmais.cdo.impl.CdoManagerFactoryImpl;
import com.buschmais.cdo.spi.bootstrap.CdoDatastoreProvider;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.spi.datastore.Datastore;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;

import com.buschmais.cdo.api.TransactionAttribute;

public class CdoBootstrapServiceImpl implements CdoBootstrapService {

    private CdoUnitFactory cdoUnitFactory;

    public CdoBootstrapServiceImpl() {
        cdoUnitFactory = new CdoUnitFactory();
    }

    @Override
    public CdoManagerFactory createCdoManagerFactory(String name) {
        CdoUnit cdoUnit = cdoUnitFactory.getCdoUnit(name);
        return createCdoManagerFactory(cdoUnit);
    }

    @Override
    public CdoManagerFactory createCdoManagerFactory(CdoUnit cdoUnit) {
        Class<?> providerType = cdoUnit.getProvider();
        if (providerType == null) {
            throw new CdoException("No provider specified for CDO unit '" + cdoUnit.getName() + "'.");
        }
        if (!CdoDatastoreProvider.class.isAssignableFrom(providerType)) {
            throw new CdoException(providerType.getName() + " specified as CDO provider must implement " + CdoDatastoreProvider.class.getName());
        }
        CdoDatastoreProvider cdoDatastoreProvider = CdoDatastoreProvider.class.cast(ClassHelper.newInstance(providerType));
        Datastore<?, ?, ?> datastore = cdoDatastoreProvider.createDatastore(cdoUnit);
        return new CdoManagerFactoryImpl(cdoUnit, datastore);
    }

}
