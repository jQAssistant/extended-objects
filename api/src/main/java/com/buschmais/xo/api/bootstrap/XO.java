package com.buschmais.xo.api.bootstrap;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManagerFactory;

import java.util.ServiceLoader;

/**
 * Provides methods for bootstrapping XO.
 */
public class XO {

    /**
     * Create a {@link com.buschmais.xo.api.XOManagerFactory} for the XO unit identified by name.
     * <p>XO units are defined in XML descriptors located as classpath resources with the name "/META-INF/xo.xml".</p>
     *
     * @param name The name of the XO unit.
     * @return The {@link com.buschmais.xo.api.XOManagerFactory}.
     */
    public static XOManagerFactory createXOManagerFactory(String name) {
        ServiceLoader<XOBootstrapService> serviceLoader = ServiceLoader.load(XOBootstrapService.class);
        for (XOBootstrapService XOBootstrapService : serviceLoader) {
            XOManagerFactory XOManagerFactory = XOBootstrapService.createXOManagerFactory(name);
            if (XOManagerFactory != null) {
                return XOManagerFactory;
            }
        }
        throw new XOException("Cannot bootstrap XO implementation.");
    }

    /**
     * Create a {@link com.buschmais.xo.api.XOManagerFactory} for the given XO unit.
     *
     * @param xoUnit The XO unit.
     * @return The {@link com.buschmais.xo.api.XOManagerFactory}.
     */
    public static XOManagerFactory createXOManagerFactory(XOUnit xoUnit) {
        ServiceLoader<XOBootstrapService> serviceLoader = ServiceLoader.load(XOBootstrapService.class);
        for (XOBootstrapService XOBootstrapService : serviceLoader) {
            XOManagerFactory XOManagerFactory = XOBootstrapService.createXOManagerFactory(xoUnit);
            if (XOManagerFactory != null) {
                return XOManagerFactory;
            }
        }
        throw new XOException("Cannot bootstrap XO implementation.");
    }

}
