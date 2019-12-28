package com.buschmais.xo.api.bootstrap;

import com.buschmais.xo.api.XOManagerFactory;

/**
 * Defines the service interface for bootstrapping the XO implementation.
 * <p>
 * It is not intended to be used directly by an application.
 * </p>
 */
public interface XOBootstrapService {

    /**
     * The resource name for XO descriptor files.
     */
    String XO_DESCRIPTOR_RESOURCE = "META-INF/xo.xml";

    /**
     * Create a {@link com.buschmais.xo.api.XOManagerFactory} using the name of a XO
     * unit.
     * <p>
     * XO units are defined in XML descriptors located as classpath resources with
     * the name "/META-INF/xo.xml".
     * </p>
     *
     * @param unit
     *            The name of the XO unit.
     * @return The {@link com.buschmais.xo.api.XOManagerFactory}.
     */
    XOManagerFactory createXOManagerFactory(String unit);

    /**
     * Create a {@link com.buschmais.xo.api.XOManagerFactory} using the name of a XO
     * unit.
     *
     * @param xoUnit
     *            The XO unit.
     * @return The {@link com.buschmais.xo.api.XOManagerFactory}.
     */
    XOManagerFactory createXOManagerFactory(XOUnit xoUnit);
}
