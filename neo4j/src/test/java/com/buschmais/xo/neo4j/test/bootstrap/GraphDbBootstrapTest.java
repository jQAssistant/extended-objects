package com.buschmais.xo.neo4j.test.bootstrap;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.api.bootstrap.XOUnitBuilder;
import com.buschmais.xo.neo4j.api.Neo4jXOProvider;
import com.buschmais.xo.neo4j.test.bootstrap.composite.A;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.net.URISyntaxException;

public class GraphDbBootstrapTest {

    @Test
    public void bootstrap() throws URISyntaxException {
        GraphDatabaseService graphDatabaseService = new TestGraphDatabaseFactory().newImpermanentDatabase();
        XOUnit xoUnit = XOUnitBuilder.create("graphDb:///", Neo4jXOProvider.class, A.class).property(GraphDatabaseService.class.getName(), graphDatabaseService).create();
        XOManagerFactory xoManagerFactory = XO.createXOManagerFactory(xoUnit);
        XOManager xoManager = xoManagerFactory.createXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setName("Test");
        xoManager.currentTransaction().commit();
        xoManager.close();
        xoManagerFactory.close();
    }

}
