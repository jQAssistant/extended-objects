package com.buschmais.cdo.neo4j.api;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.bootstrap.CdoProvider;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.impl.EmbeddedNeo4jCdoManagerFactoryImpl;

import java.net.URL;

public class Neo4jCdoProvider implements CdoProvider {

    @Override
    public CdoManagerFactory createCdoManagerFactory(CdoUnit cdoUnit) {
        URL url = cdoUnit.getUrl();
        String protocol = url.getProtocol().toLowerCase();
        if ("file".equals(protocol)) {
            return new EmbeddedNeo4jCdoManagerFactoryImpl(cdoUnit);
        }
        throw new CdoException("Unsupported url protocol '" + protocol + "' in CDO unit '" + cdoUnit.getName() + "'.");
    }
}
