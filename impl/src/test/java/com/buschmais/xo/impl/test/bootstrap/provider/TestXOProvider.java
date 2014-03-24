package com.buschmais.xo.impl.test.bootstrap.provider;

import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.spi.bootstrap.XODatastoreProvider;
import com.buschmais.xo.spi.datastore.Datastore;

public class TestXOProvider implements XODatastoreProvider {

    @Override
    public Datastore<?, ?, ?, ?, ?> createDatastore(XOUnit xoUnit) {
        return new TestXODatastore(xoUnit);
    }
}
