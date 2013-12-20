package com.buschmais.cdo.api.bootstrap;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoManagerFactory;

import java.net.URL;
import java.util.Properties;
import java.util.ServiceLoader;

import com.buschmais.cdo.api.TransactionAttribute;

import com.buschmais.cdo.api.ValidationMode;

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

    public static CdoManagerFactory createCdoManagerFactory(URL url, Class<?> provider, Class<?>[] types) {
        CdoUnit cdoUnit = new CdoUnit("default", "Default CDO unit", url, provider, types, ValidationMode.AUTO, TransactionAttribute.MANDATORY, new Properties());
        return createCdoManagerFactory(cdoUnit);
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
