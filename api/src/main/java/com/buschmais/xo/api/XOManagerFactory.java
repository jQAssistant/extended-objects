package com.buschmais.xo.api;

import com.buschmais.xo.api.bootstrap.XOUnit;

/**
 * Defines the factory interfaces for {@link XOManager} instances.
 */
public interface XOManagerFactory extends AutoCloseable, CloseSupport {

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

    /**
     * Reutrn the underlying configured datastore.
     *
     * @param datastoreType
     *            The expected datastore type.
     * @param <DS>
     *            The datastore type.
     * @return The datastore.
     */
    <DS> DS getDatastore(Class<DS> datastoreType);

}
