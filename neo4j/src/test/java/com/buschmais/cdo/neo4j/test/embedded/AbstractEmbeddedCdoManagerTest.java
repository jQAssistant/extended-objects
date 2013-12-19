package com.buschmais.cdo.neo4j.test.embedded;

import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.ValidationMode;
import com.buschmais.cdo.api.bootstrap.Cdo;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.api.Neo4jCdoProvider;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Properties;

public abstract class AbstractEmbeddedCdoManagerTest extends AbstractCdoManagerTest {

    @Override
    protected CdoManagerFactory getNeo4jCdoManagerFactory(Class<?>[] types) throws MalformedURLException {
        CdoUnit cdoUnit = new CdoUnit("embedded", "Embedded CDO unit", new File("target/neo4j/embedded").toURI().toURL(), Neo4jCdoProvider.class, types, ValidationMode.AUTO, getTransactionAttribute(), new Properties());
        return Cdo.createCdoManagerFactory(cdoUnit);
    }

}
