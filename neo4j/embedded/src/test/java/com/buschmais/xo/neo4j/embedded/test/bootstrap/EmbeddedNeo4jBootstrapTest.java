package com.buschmais.xo.neo4j.embedded.test.bootstrap;

import org.junit.Test;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.neo4j.embedded.test.bootstrap.composite.A;

public class EmbeddedNeo4jBootstrapTest {

    @Test
    public void bootstrap() {
        XOManagerFactory xoManagerFactory = XO.createXOManagerFactory("Neo4jEmbedded");
        XOManager xoManager = xoManagerFactory.createXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setName("Test");
        xoManager.currentTransaction().commit();
        xoManager.close();
        xoManagerFactory.close();
    }

}
