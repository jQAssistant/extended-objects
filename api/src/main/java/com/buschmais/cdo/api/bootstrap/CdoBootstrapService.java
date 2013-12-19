package com.buschmais.cdo.api.bootstrap;

import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.TransactionAttribute;
import com.buschmais.cdo.api.ValidationMode;

import java.net.URL;
import java.util.Properties;

public interface CdoBootstrapService {

    CdoManagerFactory createCdoManagerFactory(String unit);

    CdoManagerFactory createCdoManagerFactory(CdoUnit cdoUnit);
}
