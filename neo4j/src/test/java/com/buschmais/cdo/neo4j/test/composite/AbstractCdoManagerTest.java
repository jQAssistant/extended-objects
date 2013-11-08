package com.buschmais.cdo.neo4j.test.composite;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.neo4j.impl.EmbeddedNeo4jCdoManagerFactoryImpl;
import com.buschmais.cdo.neo4j.test.composite.basic.A;
import com.buschmais.cdo.neo4j.test.composite.basic.B;
import com.buschmais.cdo.neo4j.test.composite.basic.C;
import com.buschmais.cdo.neo4j.test.composite.basic.D;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.File;
import java.net.MalformedURLException;

public abstract class AbstractCdoManagerTest {

    private CdoManagerFactory cdoManagerFactory;

    @Before
    public void createNodeManagerFactory() throws MalformedURLException {
        cdoManagerFactory = new EmbeddedNeo4jCdoManagerFactoryImpl(new File("target/neo4j").toURI().toURL(), getTypes());
        CdoManager cdoManager = getCdoManagerFactory().createCdoManager();
        cdoManager.begin();
        cdoManager.executeQuery("MATCH (n)-[r]-(d) DELETE r");
        cdoManager.executeQuery("MATCH (n) DELETE n");
        cdoManager.commit();
    }

    @After
    public void closeNodeManagerFactory() {
        cdoManagerFactory.close();
    }

    protected CdoManagerFactory getCdoManagerFactory() {
        return cdoManagerFactory;
    }

    protected abstract Class<?>[] getTypes();
}
