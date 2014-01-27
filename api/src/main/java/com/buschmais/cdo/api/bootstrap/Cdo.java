package com.buschmais.cdo.api.bootstrap;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoManagerFactory;

import java.util.ServiceLoader;

/**
 * Provides methods for bootstrapping CDO.
 */
public class Cdo {

    /**
     * Create a {@link CdoManagerFactory} for the CDO unit identified by name.
     * <p>CDO units are defined in XML descriptors located as classpath resources with the name "/META-INF/cdo.xml".</p>
     *
     * @param name The name of the CDO unit.
     * @return The {@link CdoManagerFactory}.
     */
    public static CdoManagerFactory createCdoManagerFactory(String name) {
        ServiceLoader<CdoBootstrapService> serviceLoader = ServiceLoader.load(CdoBootstrapService.class);
        for (CdoBootstrapService cdoBootstrapService : serviceLoader) {
            CdoManagerFactory cdoManagerFactory = cdoBootstrapService.createCdoManagerFactory(name);
            if (cdoManagerFactory != null) {
                return cdoManagerFactory;
            }
        }
        throw new CdoException("Cannot bootstrap CDO implementation.");
    }

    /**
     * Create a {@link CdoManagerFactory} for the given CDO unit.
     *
     * @param cdoUnit The CDO unit.
     * @return The {@link CdoManagerFactory}.
     */
    public static CdoManagerFactory createCdoManagerFactory(CdoUnit cdoUnit) {
        ServiceLoader<CdoBootstrapService> serviceLoader = ServiceLoader.load(CdoBootstrapService.class);
        for (CdoBootstrapService cdoBootstrapService : serviceLoader) {
            CdoManagerFactory cdoManagerFactory = cdoBootstrapService.createCdoManagerFactory(cdoUnit);
            if (cdoManagerFactory != null) {
                return cdoManagerFactory;
            }
        }
        throw new CdoException("Cannot bootstrap CDO implementation.");
    }

}
