package com.buschmais.xo.neo4j.test;

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.Transaction;
import com.buschmais.xo.api.ValidationMode;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.api.Neo4jXOProvider;
import com.buschmais.xo.test.AbstractXOManagerTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServer;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest.Neo4jDatabase.MEMORY;
import static com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest.Neo4jDatabase.REST;

public abstract class AbstractNeo4jXOManagerTest extends AbstractXOManagerTest {

    public enum Neo4jDatabase implements AbstractXOManagerTest.Database {
        MEMORY("memory:///"),
        REST("http://localhost:7474/db/data");
        private URI uri;

        private Neo4jDatabase(String uri) {
            try {
                this.uri = new URI(uri);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public URI getUri() {
            return uri;
        }

        @Override
        public Class<?> getProvider() {
            return Neo4jXOProvider.class;
        }
    }

    private static WrappingNeoServer server;

    protected AbstractNeo4jXOManagerTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @BeforeClass
    public static void startServer() {
        GraphDatabaseService graphDatabaseService = new TestGraphDatabaseFactory().newImpermanentDatabase();
        server = new WrappingNeoServer((GraphDatabaseAPI) graphDatabaseService);
        server.start();
    }

    protected static Collection<Object[]> xoUnits(Class<?>... types) {
        return xoUnits(Arrays.asList(MEMORY, REST), Arrays.asList(types), Collections.<Class<?>>emptyList(), ValidationMode.AUTO, ConcurrencyMode.SINGLETHREADED, Transaction.TransactionAttribute.NONE);
    }

    protected static Collection<Object[]> xoUnits(List<? extends Class<?>> types, List<? extends Class<?>> instanceListeners, ValidationMode validationMode, ConcurrencyMode concurrencyMode, Transaction.TransactionAttribute transactionAttribute) {
        return xoUnits(Arrays.asList(MEMORY, REST), types, instanceListeners, validationMode, concurrencyMode, transactionAttribute);
    }

    @AfterClass
    public static void stopServer() {
        server.stop();
    }

    protected void dropDatabase() {
        XOManager manager = getXoManager();
        manager.currentTransaction().begin();
        manager.createQuery("MATCH (n)-[r]-() DELETE r").execute();
        manager.createQuery("MATCH (n) DELETE n").execute();
        manager.currentTransaction().commit();
    }
}
