package com.buschmais.xo.api;

import com.buschmais.xo.api.bootstrap.XOUnit;

/**
 * Defines the factory interfaces for {@link XOManager} instances.
 */
public interface XOManagerFactory extends AutoCloseable {

    /**
     * The persistent identifier (PID).
     */
    public static final String FACTORY_PID = "com.buschmais.cdo.factory";

    /**
     * Create a {@link XOManager} instance.
     *
     * @return The {@link XOManager} instance.
     */
    XOManager createXOManager();

    /**
     * Close this factory.
     */
    void close();

    /**
     * Return the datastore configuration object used to initialize this factory.
     *
     * @return The underlying configuration.
     */
    XOUnit getXOUnit();

}
