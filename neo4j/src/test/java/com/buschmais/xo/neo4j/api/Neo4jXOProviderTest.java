package com.buschmais.xo.neo4j.api;

import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.impl.datastore.EmbeddedNeo4jDatastore;
import com.buschmais.xo.neo4j.impl.datastore.RestNeo4jDatastore;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class Neo4jXOProviderTest {

    private final Neo4jXOProvider provider = new Neo4jXOProvider();

    @Test
    public void lookupTests() throws Exception {

        assertEquals(FileDatastoreFactory.class, provider.lookupFactory(new URI("file://foo/")).getClass());

        assertEquals(MemoryDatastoreFactory.class, provider.lookupFactory(new URI("memory:///")).getClass());

        assertEquals(HttpDatastoreFactory.class, provider.lookupFactory(new URI("http://foo")).getClass());
        assertEquals(HttpsDatastoreFactory.class, provider.lookupFactory(new URI("https://foo")).getClass());
    }

    @Test
    public void createDsTests() throws Exception {
        assertEquals(EmbeddedNeo4jDatastore.class, provider.createDatastore(unit("memory:///")).getClass());
        assertEquals(RestNeo4jDatastore.class, provider.createDatastore(unit("http://foo")).getClass());
    }

    private XOUnit unit(String uri) throws Exception {
        return new XOUnit(null, null, new URI(uri), null, new Class<?>[0], null, null, null, null, null);
    }
}
