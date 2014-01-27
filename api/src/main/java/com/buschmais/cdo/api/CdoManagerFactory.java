package com.buschmais.cdo.api;

import com.buschmais.cdo.api.bootstrap.CdoUnit;

/**
 * Defines the factory interfaces for {@link CdoManager} instances.
 */
public interface CdoManagerFactory extends AutoCloseable {

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

    /**
     * Return the datastore configuration object used to initialize this factory.
     *
     * @return The underlying configuration.
     */
    CdoUnit getCdoUnit();

}
