package com.buschmais.xo.neo4j.test.query;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import static org.neo4j.graphdb.DynamicLabel.label;

import java.net.URISyntaxException;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.schema.IndexDefinition;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.embedded.api.EmbeddedNeo4jDatastoreSession;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.Neo4jDatabase;
import com.buschmais.xo.neo4j.test.query.composite.A;
import com.buschmais.xo.neo4j.test.query.composite.C;

@RunWith(Parameterized.class)
public class CreateIndexTest extends AbstractNeo4jXOManagerTest {

    public CreateIndexTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(A.class, C.class);
    }

    @Test
    public void createIndex() {
        assumeThat(getXoManagerFactory().getXOUnit().getProvider(), equalTo(Neo4jDatabase.MEMORY.getProvider()));
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        GraphDatabaseService graphDatabaseService = xoManager.getDatastoreSession(EmbeddedNeo4jDatastoreSession.class).getGraphDatabaseService();
        assertThat(findIndex(graphDatabaseService, label("A"), "value"), notNullValue());
        assertThat(findIndex(graphDatabaseService, label("C"), "value"), notNullValue());
        xoManager.currentTransaction().commit();
    }

    /**
     * Find an index.
     *
     * @param graphDatabaseService
     *            The Graph database service.
     * @param label
     *            The label.
     * @param propertyName
     *            The property name.
     * @return The index or <code>null</code>:
     */
    private IndexDefinition findIndex(GraphDatabaseService graphDatabaseService, Label label, String propertyName) {
        final Iterable<IndexDefinition> indexes = graphDatabaseService.schema().getIndexes(label);
        for (IndexDefinition indexDefinition : indexes) {
            for (String key : indexDefinition.getPropertyKeys()) {
                if (key.equals(propertyName)) {
                    return indexDefinition;
                }
            }
        }
        return null;
    }
}
