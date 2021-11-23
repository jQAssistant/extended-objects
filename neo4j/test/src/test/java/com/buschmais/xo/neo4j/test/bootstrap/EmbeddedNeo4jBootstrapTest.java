package com.buschmais.xo.neo4j.test.bootstrap;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.neo4j.embedded.api.EmbeddedNeo4jDatastoreSession;
import com.buschmais.xo.neo4j.test.bootstrap.composite.A;
import com.buschmais.xo.spi.datastore.DatastoreSession;

import org.junit.Test;

public class EmbeddedNeo4jBootstrapTest {

    @Test
    public void bootstrap() {
        XOManagerFactory xoManagerFactory = XO.createXOManagerFactory("Neo4jEmbedded");
        assertThat(xoManagerFactory, notNullValue());
        XOManager xoManager = xoManagerFactory.createXOManager();
        assertThat(xoManager.getDatastoreSession(DatastoreSession.class), instanceOf(EmbeddedNeo4jDatastoreSession.class));
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setName("Test");
        xoManager.currentTransaction().commit();
        xoManager.close();
        xoManagerFactory.close();
    }

}
