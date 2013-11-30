package com.buschmais.cdo.neo4j.test.embedded;

import com.buschmais.cdo.neo4j.impl.AbstractNeo4jCdoManagerFactoryImpl;
import com.buschmais.cdo.neo4j.impl.EmbeddedNeo4jCdoManagerFactoryImpl;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;

import java.io.File;
import java.net.MalformedURLException;

public abstract class AbstractEmbeddedCdoManagerTest extends AbstractCdoManagerTest {

    @Override
    protected AbstractNeo4jCdoManagerFactoryImpl getNeo4jCdoManagerFactory(Class<?>[] types) throws MalformedURLException {
        return new EmbeddedNeo4jCdoManagerFactoryImpl(createCdoUnit("file://target/neo4j/embedded", getTypes()));
    }

}
