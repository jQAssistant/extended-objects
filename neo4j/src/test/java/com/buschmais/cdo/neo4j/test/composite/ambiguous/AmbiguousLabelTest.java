package com.buschmais.cdo.neo4j.test.composite.ambiguous;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.QueryResult;
import com.buschmais.cdo.neo4j.impl.EmbeddedNeo4jCdoManagerFactoryImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;

public class AmbiguousLabelTest {

    private static CdoManagerFactory cdoManagerFactory;

    @BeforeClass
    public static void createNodeManagerFactory() throws MalformedURLException {
        cdoManagerFactory = new EmbeddedNeo4jCdoManagerFactoryImpl(new File("target/neo4j").toURI().toURL(), A.class, B.class);
        CdoManager cdoManager = cdoManagerFactory.createCdoManager();
        cdoManager.begin();
        cdoManager.executeQuery("MATCH (n)-[r]-(d) DELETE r");
        cdoManager.executeQuery("MATCH (n) DELETE n");
        cdoManager.commit();
        cdoManager.close();
    }

    @AfterClass
    public static void closeNodeManagerFactory() {
        cdoManagerFactory.close();
    }

    @Test @Ignore
    public void ambiguousLabels() {
        CdoManager cdoManager = cdoManagerFactory.createCdoManager();
        cdoManager.begin();
        A a = cdoManager.create(A.class);
        a.setIndex("1");
        a.setEnumeration(Enumeration.B);
        cdoManager.create(B.class);
        cdoManager.commit();
        cdoManager.close();
        cdoManager = cdoManagerFactory.createCdoManager();
        cdoManager.begin();
        QueryResult queryResult = cdoManager.executeQuery("MATCH (n:B) RETURN n");
        for (QueryResult.Row row : queryResult.getRows()) {
        }
        cdoManager.commit();
        cdoManager.close();
    }
}
