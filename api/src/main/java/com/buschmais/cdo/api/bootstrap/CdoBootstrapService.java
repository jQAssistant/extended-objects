package com.buschmais.cdo.api.bootstrap;

import com.buschmais.cdo.api.CdoManagerFactory;

import java.net.URL;
import java.util.Properties;
import java.util.Set;

public interface CdoBootstrapService {

    CdoManagerFactory createCdoManagerFactory(String unit);

    CdoManagerFactory createCdoManagerFactory(URL url, Class<?> provider, Class<?>[] types, CdoManagerFactory.ValidationMode validationMode, CdoManagerFactory.TransactionAttribute transactionAttribute, Properties properties);
}
