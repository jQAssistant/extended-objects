package com.buschmais.cdo.impl.test.bootstrap.provider;

import com.buschmais.cdo.spi.bootstrap.CdoDatastoreProvider;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.spi.datastore.Datastore;

public class TestCdoProvider implements CdoDatastoreProvider {

    @Override
    public Datastore<?, ? ,?> createDatastore(CdoUnit cdoUnit) {
        return new TestCdoDatastore(cdoUnit);
    }
}
