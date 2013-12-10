package com.buschmais.cdo.api.bootstrap;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoManagerFactory;

import java.net.URL;
import java.util.Properties;
import java.util.ServiceLoader;

import static com.buschmais.cdo.api.CdoManagerFactory.TransactionAttribute;
import static com.buschmais.cdo.api.CdoManagerFactory.ValidationMode;

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

    public static CdoManagerFactory createCdoManagerFactory(URL url, Class<?> provider, Class<?>[] types, ValidationMode validationMode, TransactionAttribute transactionAttribute, Properties properties) {
        ServiceLoader<CdoBootstrapService> serviceLoader = ServiceLoader.load(CdoBootstrapService.class);
        for (CdoBootstrapService cdoBootstrapService : serviceLoader) {
            CdoManagerFactory cdoManagerFactory = cdoBootstrapService.createCdoManagerFactory(url, provider, types, validationMode, transactionAttribute, properties);
            if (cdoManagerFactory != null) {
                return cdoManagerFactory;
            }
        }
        throw new CdoException("Cannot bootstrap CDO implementation.");
    }
}
