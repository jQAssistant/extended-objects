package com.buschmais.xo.spi.trace;

import com.buschmais.xo.spi.datastore.DatastoreTransaction;

/**
 * {@link DatastoreTransaction} implementation allowing tracing on delegates.
 */
public class TraceDatastoreTransaction implements DatastoreTransaction {

    private DatastoreTransaction delegate;

    public TraceDatastoreTransaction(DatastoreTransaction delegate) {
        this.delegate = delegate;
    }

    @Override
    public void begin() {
        delegate.begin();
    }

    @Override
    public void commit() {
        delegate.commit();
    }

    @Override
    public void rollback() {
        delegate.rollback();
    }

    @Override
    public boolean isActive() {
        return delegate.isActive();
    }
}
