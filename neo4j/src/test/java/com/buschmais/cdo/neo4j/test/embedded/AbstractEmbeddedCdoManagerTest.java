package com.buschmais.cdo.neo4j.test.embedded;

import com.buschmais.cdo.api.ValidationMode;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.api.Neo4jCdoProvider;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

public abstract class AbstractEmbeddedCdoManagerTest extends AbstractCdoManagerTest {

    @Override
    protected CdoUnit getCdoUnit(Class<?>[] types) throws URISyntaxException {
        return new CdoUnit("embedded", "Embedded CDO unit", new URI("memory:///"), Neo4jCdoProvider.class,
                types, getInstanceListenerTypes(), ValidationMode.AUTO, getConcurrencyMode(), getTransactionAttribute(), new Properties());
    }

}
