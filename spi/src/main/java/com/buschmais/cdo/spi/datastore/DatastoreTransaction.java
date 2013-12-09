package com.buschmais.cdo.spi.datastore;

public interface DatastoreTransaction {

    void begin();

    void commit();

    void rollback();

    boolean isActive();
}
