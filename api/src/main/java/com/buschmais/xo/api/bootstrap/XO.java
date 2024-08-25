package com.buschmais.xo.api.bootstrap;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManagerFactory;

/**
 * Provides methods for bootstrapping XO.
 */
public final class XO {

    private XO() {
    }

    /**
     * Create a {@link com.buschmais.xo.api.XOManagerFactory} for the XO unit
     * identified by name.
     * <p>
     * XO units are defined in XML descriptors located as classpath resources with
     * the name "/META-INF/xo.xml".
     * </p>
     *
     * @param name
     *     The name of the XO unit.
     * @return The {@link com.buschmais.xo.api.XOManagerFactory}.
     */
    public static XOManagerFactory<?, ?, ?, ?> createXOManagerFactory(String name) {
        Optional<XOBootstrapService> bootstrapService = getBootstrapService();
        if (bootstrapService.isPresent()) {
            return bootstrapService.get()
                .createXOManagerFactory(name);
        }
        throw new XOException("Cannot bootstrap XO implementation.");
    }

    /**
     * Create a {@link com.buschmais.xo.api.XOManagerFactory} for the given XO unit.
     *
     * @param xoUnit
     *     The XO unit.
     * @return The {@link com.buschmais.xo.api.XOManagerFactory}.
     */
    public static XOManagerFactory<?, ?, ?, ?> createXOManagerFactory(XOUnit xoUnit) {
        Optional<XOBootstrapService> bootstrapService = getBootstrapService();
        if (bootstrapService.isPresent()) {
            return bootstrapService.get()
                .createXOManagerFactory(xoUnit);
        }
        throw new XOException("Cannot bootstrap XO implementation.");
    }

    private static Optional<XOBootstrapService> getBootstrapService() {
        ServiceLoader<XOBootstrapService> serviceLoader = ServiceLoader.load(XOBootstrapService.class);
        return StreamSupport.stream(serviceLoader.spliterator(), false)
            .findFirst();
    }

}
