package com.buschmais.cdo.spi.bootstrap;

import com.buschmais.cdo.spi.datastore.Datastore;

public interface CdoDatastoreProvider {

    Datastore<?> createDatastore(CdoUnit cdoUnit);

}
