package com.buschmais.cdo.impl.bootstrap;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.ValidationMode;
import com.buschmais.cdo.api.bootstrap.CdoBootstrapService;
import com.buschmais.cdo.impl.CdoManagerFactoryImpl;
import com.buschmais.cdo.spi.bootstrap.CdoDatastoreProvider;
import com.buschmais.cdo.spi.bootstrap.CdoUnit;
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
        Class<? extends CdoDatastoreProvider> providerType = cdoUnit.getProvider();
        return createCdoManagerFactory(cdoUnit, providerType);
    }

    @Override
    public CdoManagerFactory createCdoManagerFactory(URL url, Class<?> provider, Class<?>[] types, ValidationMode validationMode, TransactionAttribute transactionAttribute, Properties properties) {
        Class<? extends CdoDatastoreProvider> providerType = (Class<? extends CdoDatastoreProvider>) provider;
        CdoUnit cdoUnit = new CdoUnit("default", "Default CDO unit.", url, providerType, new HashSet<>(Arrays.asList(types)), validationMode, transactionAttribute, properties);
        return createCdoManagerFactory(cdoUnit, providerType);
    }

    private CdoManagerFactory createCdoManagerFactory(CdoUnit cdoUnit, Class<? extends CdoDatastoreProvider> providerType) {
        if (providerType == null) {
            throw new CdoException("No provider specified for CDO unit '" + cdoUnit.getName() + "'.");
        }
        CdoDatastoreProvider cdoDatastoreProvider = ClassHelper.newInstance(providerType);
        Datastore<?, ?, ?> datastore = cdoDatastoreProvider.createDatastore(cdoUnit);
        return new CdoManagerFactoryImpl(cdoUnit, datastore);
    }

}
