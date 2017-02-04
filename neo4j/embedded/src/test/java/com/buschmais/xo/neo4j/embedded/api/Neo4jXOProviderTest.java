package com.buschmais.xo.neo4j.embedded.api;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;

import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.embedded.impl.datastore.EmbeddedNeo4jDatastore;

public class Neo4jXOProviderTest {

    private final Neo4jXOProvider provider = new Neo4jXOProvider();

    @Test
    public void lookupTests() throws Exception {
        assertEquals(FileDatastoreFactory.class, provider.lookupFactory(new URI("file://foo/")).getClass());
        assertEquals(MemoryDatastoreFactory.class, provider.lookupFactory(new URI("memory:///")).getClass());
        assertEquals(GraphDbDatastoreFactory.class, provider.lookupFactory(new URI("graphDb:///")).getClass());
    }

    @Test
    public void createDsTests() throws Exception {
        assertEquals(EmbeddedNeo4jDatastore.class, provider.createDatastore(unit("memory:///")).getClass());
    }

    private XOUnit unit(String uri) throws Exception {
        return new XOUnit(null, null, new URI(uri), null, new Class<?>[0], null, null, null, null, null);
    }
}
