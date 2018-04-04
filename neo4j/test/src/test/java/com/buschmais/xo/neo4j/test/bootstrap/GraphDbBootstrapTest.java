package com.buschmais.xo.neo4j.test.bootstrap;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.embedded.api.EmbeddedNeo4jXOProvider;
import com.buschmais.xo.neo4j.test.bootstrap.composite.A;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Properties;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.neo4j.graphdb.DynamicLabel.label;

public class GraphDbBootstrapTest {

    @Test
    public void bootstrap() throws URISyntaxException {
        GraphDatabaseService graphDatabaseService = new TestGraphDatabaseFactory().newImpermanentDatabase();
        Properties properties=new Properties();
        properties.put(GraphDatabaseService.class.getName(), graphDatabaseService);
        XOUnit xoUnit = XOUnit.builder().uri(new URI("graphDb:///")).provider(EmbeddedNeo4jXOProvider.class).types(Collections.singletonList(A.class)).build();
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
