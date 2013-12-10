package com.buschmais.cdo.neo4j.test.embedded;

import com.buschmais.cdo.impl.AbstractCdoManagerFactoryImpl;
import com.buschmais.cdo.neo4j.impl.EmbeddedNeo4jCdoManagerFactoryImpl;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;

import java.io.File;
import java.net.MalformedURLException;

public abstract class AbstractEmbeddedCdoManagerTest extends AbstractCdoManagerTest {

    @Override
    protected AbstractCdoManagerFactoryImpl getNeo4jCdoManagerFactory(Class<?>[] types) throws MalformedURLException {
        return new EmbeddedNeo4jCdoManagerFactoryImpl(createCdoUnit(new File("target/neo4j/embedded").toURI().toURL(), getTypes()));
    }

}
