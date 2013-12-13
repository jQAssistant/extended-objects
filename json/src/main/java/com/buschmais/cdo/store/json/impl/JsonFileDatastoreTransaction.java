package com.buschmais.cdo.store.json.impl;

import com.buschmais.cdo.spi.datastore.DatastoreTransaction;

public class JsonFileDatastoreTransaction implements DatastoreTransaction {

    @Override
    public void begin() {
    }

    @Override
    public void commit() {
    }

    @Override
    public void rollback() {
    }

    @Override
    public boolean isActive() {
        return false;
    }
}
