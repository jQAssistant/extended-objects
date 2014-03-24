package com.buschmais.xo.neo4j.test.bootstrap;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.neo4j.test.bootstrap.composite.A;
import org.junit.Test;

public class EmbeddedNeo4jBootstrapTest {

    @Test
    public void bootstrap() {
        XOManagerFactory XOManagerFactory = XO.createXOManagerFactory("Neo4jEmbedded");
        XOManager XOManager = XOManagerFactory.createXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        a.setName("Test");
        XOManager.currentTransaction().commit();
        XOManager.close();
        XOManagerFactory.close();
    }

}
