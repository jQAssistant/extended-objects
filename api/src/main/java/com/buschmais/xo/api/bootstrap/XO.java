package com.buschmais.xo.api.bootstrap;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManagerFactory;

import java.util.ServiceLoader;

/**
 * Provides methods for bootstrapping CDO.
 */
public class XO {

    /**
     * Create a {@link com.buschmais.xo.api.XOManagerFactory} for the CDO unit identified by name.
     * <p>CDO units are defined in XML descriptors located as classpath resources with the name "/META-INF/cdo.xml".</p>
     *
     * @param name The name of the CDO unit.
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
        throw new XOException("Cannot bootstrap CDO implementation.");
    }

    /**
     * Create a {@link com.buschmais.xo.api.XOManagerFactory} for the given CDO unit.
     *
     * @param XOUnit The CDO unit.
     * @return The {@link com.buschmais.xo.api.XOManagerFactory}.
     */
    public static XOManagerFactory createXOManagerFactory(XOUnit XOUnit) {
        ServiceLoader<XOBootstrapService> serviceLoader = ServiceLoader.load(XOBootstrapService.class);
        for (XOBootstrapService XOBootstrapService : serviceLoader) {
            XOManagerFactory XOManagerFactory = XOBootstrapService.createXOManagerFactory(XOUnit);
            if (XOManagerFactory != null) {
                return XOManagerFactory;
            }
        }
        throw new XOException("Cannot bootstrap CDO implementation.");
    }

}
