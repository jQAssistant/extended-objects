package com.buschmais.xo.api;

/**
 * Defines the interface for {@link XOManager} transactions.
 */
public interface XOTransaction extends AutoCloseable {

    /**
     * Defines a transaction lifecycle callback which can be registered.
     */
    interface Synchronization {

        /**
         * Called before the transaction is completed.
         */
        void beforeCompletion();

        /**
         * Called after the transaction has been completed.
         *
         * @param committed
         *     <code>true</code> if the transaction as been committed,
         *     <code>false</code> if the transaction was rolled back.
         */
        void afterCompletion(boolean committed);
    }

    /**
     * Begin a transaction.
     */
    XOTransaction begin();

    /**
     * Commit all changes of the current transaction.
     */
    void commit();

    /**
     * Rollback all changes from the current transaction.
     */
    void rollback();

    /**
     * Determine the state of the transaction associated with this
     * {@link XOManager}.
     *
     * @return <code>true</code> if there is an active transaction.
     */
    boolean isActive();

    /**
     * Mark the current transaction as rollback only.
     */
    void setRollbackOnly();

    /**
     * Close the transaction, i.e. commit the transaction if it has not been marked
     * for rollback, perform rollback otherwise.
     */
    @Override
    void close();

    /**
     * Returns if the current transaction is marked as rollback only.
     *
     * @return <code>true</code> if the current transaction is marked as rollback
     * only.
     */
    boolean isRollbackOnly();

    /**
     * Register a {@link Synchronization}.
     *
     * @param synchronization
     *     The a {@link Synchronization}.
     */
    void registerSynchronization(Synchronization synchronization);

    /**
     * Unregister a {@link Synchronization}.
     *
     * @param synchronization
     *     The a {@link Synchronization}.
     */
    void unregisterSynchronization(Synchronization synchronization);
}
