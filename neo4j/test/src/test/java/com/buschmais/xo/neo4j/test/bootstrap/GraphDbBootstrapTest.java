package com.buschmais.xo.neo4j.test.bootstrap;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;
import static org.neo4j.graphdb.Label.label;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.embedded.api.EmbeddedNeo4jXOProvider;
import com.buschmais.xo.neo4j.test.bootstrap.composite.A;

import org.junit.Test;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestDatabaseManagementServiceBuilder;

public class GraphDbBootstrapTest {

    @Test
    public void bootstrap() throws URISyntaxException {
        DatabaseManagementService managementService = new TestDatabaseManagementServiceBuilder().impermanent().build();
        GraphDatabaseService graphDatabaseService = managementService.database(DEFAULT_DATABASE_NAME);
        Properties properties = new Properties();
        properties.put(GraphDatabaseService.class.getName(), graphDatabaseService);
        XOUnit xoUnit = XOUnit.builder().uri(new URI("graphDb:///")).provider(EmbeddedNeo4jXOProvider.class).types(singletonList(A.class))
                .properties(properties).build();
        XOManagerFactory xoManagerFactory = XO.createXOManagerFactory(xoUnit);
        XOManager xoManager = xoManagerFactory.createXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setName("Test");
        xoManager.currentTransaction().commit();
        xoManager.close();
        xoManagerFactory.close();
        try (Transaction transaction = graphDatabaseService.beginTx()) {
            ResourceIterator<Node> iterator = transaction.findNodes(label("A"), "name", "Test");
            assertThat(iterator.hasNext(), equalTo(true));
            Node node = iterator.next();
            assertThat(node.hasLabel(label("A")), equalTo(true));
            assertThat(node.getProperty("name"), equalTo((Object) "Test"));
            transaction.commit();
        }
    }

}
