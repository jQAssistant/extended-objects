package com.buschmais.xo.neo4j.test.bootstrap;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.neo4j.graphdb.DynamicLabel.label;

import java.net.URISyntaxException;

import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.api.bootstrap.XOUnitBuilder;
import com.buschmais.xo.neo4j.embedded.api.Neo4jXOProvider;
import com.buschmais.xo.neo4j.test.bootstrap.composite.A;

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
        try (Transaction transaction = graphDatabaseService.beginTx()) {
            ResourceIterator<Node> iterator = graphDatabaseService.findNodes(label("A"), "name", "Test");
            assertThat(iterator.hasNext(), equalTo(true));
            Node node = iterator.next();
            assertThat(node.hasLabel(label("A")), equalTo(true));
            assertThat(node.getProperty("name"), equalTo((Object) "Test"));
            transaction.success();
        }
    }

}
