package com.buschmais.cdo.api.bootstrap;

import com.buschmais.cdo.api.CdoManagerFactory;

/**
 * Defines the service interface for bootstrapping the CDO implementation.
 * <p>It is not intended to be used directly by an application.</p>
 */
public interface CdoBootstrapService {

    /**
     * Create a {@link CdoManagerFactory} using the name of a CDO unit.
     * <p>CDO units are defined in XML descriptors located as classpath resources with the name "/META-INF/cdo.xml".</p>
     *
     * @param unit The name of the CDO unit.
     * @return The {@link CdoManagerFactory}.
     */
    CdoManagerFactory createCdoManagerFactory(String unit);

    /**
     * Create a {@link CdoManagerFactory} using the name of a CDO unit.
     *
     * @param cdoUnit The CDO unit.
     * @return The {@link CdoManagerFactory}.
     */
    CdoManagerFactory createCdoManagerFactory(CdoUnit cdoUnit);
}
