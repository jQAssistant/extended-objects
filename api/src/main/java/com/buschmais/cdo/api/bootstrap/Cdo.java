package com.buschmais.cdo.api.bootstrap;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoManagerFactory;

import java.util.ServiceLoader;

public class Cdo {

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
