package com.buschmais.cdo.api;

/**
 * Defines the interface for {@link com.buschmais.cdo.api.CdoManager} transactions.
 */
public interface CdoTransaction {

    public interface Synchronization {
        void beforeCompletion();

        void afterCompletion(boolean committed);
    }

    /**
     * Begin a transaction.
     */
    void begin();

    /**
     * Commit all changes of the current transaction.
     */
    void commit();

    /**
     * Rollback all changes from the current transaction.
     */
    void rollback();

    /**
     * Determine the state of the transaction associated with this {@link com.buschmais.cdo.api.CdoManager}.
     *
     * @return <code>true</code> if there  is an active transaction.
     */
    boolean isActive();

    /**
     * Register a {@link Synchronization}.
     *
     * @param synchronization The a {@link Synchronization}.
     */
    void registerSynchronization(Synchronization synchronization);
}
