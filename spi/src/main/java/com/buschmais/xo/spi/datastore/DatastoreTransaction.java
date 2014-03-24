package com.buschmais.xo.spi.datastore;

/**
 * Represents a transaction on the datastore.
 * <p>It is associated to a {@link DatastoreSession}.</p>
 */
public interface DatastoreTransaction {

    /**
     * Begin a transaction.
     */
    void begin();

    /**
     * Complete a currently active transaction committing all pending changes.
     */
    void commit();

    /**
     * Complete a currently active transaction rolling back all pending changes.
     */
    void rollback();

    /**
     * Determine if the transaction is currently active.
     *
     * @return <code>true</code> if the transaction is currently active, <code>false</code> otherwise.
     */
    boolean isActive();
}
