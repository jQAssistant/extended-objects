package com.buschmais.cdo.api.bootstrap;

import com.buschmais.cdo.api.CdoManagerFactory;

public interface CdoProvider {

    CdoManagerFactory createCdoManagerFactory(CdoUnit cdoUnit);

}
