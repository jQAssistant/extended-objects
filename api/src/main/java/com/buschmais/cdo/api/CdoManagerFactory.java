package com.buschmais.cdo.api;

/**
 * Defines the factory interfaces for {@link CdoManager} instances.
 */
public interface CdoManagerFactory {

    public enum ValidationMode {
        NONE, AUTO;
    }

    public enum TransactionAttribute {
        MANDATORY,
        REQUIRES;
    }

    /**
     * Create a {@link CdoManager} instance.
     *
     * @return The {@link CdoManager} instance.
     */
    CdoManager createCdoManager();

    /**
     * Close this factory.
     */
    void close();

}
