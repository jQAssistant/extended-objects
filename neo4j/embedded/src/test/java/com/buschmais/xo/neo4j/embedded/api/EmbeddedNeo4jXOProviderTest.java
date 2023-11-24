package com.buschmais.xo.neo4j.embedded.api;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.embedded.impl.datastore.EmbeddedDatastore;

import org.junit.Test;

public class EmbeddedNeo4jXOProviderTest {

    private final EmbeddedNeo4jXOProvider provider = new EmbeddedNeo4jXOProvider();

    @Test
    public void lookupTests() throws Exception {
        assertEquals(FileDatabaseManagementServiceFactory.class, provider.lookupFactory(new URI("file://foo/")).getClass());
        assertEquals(MemoryDatabaseManagementServiceFactory.class, provider.lookupFactory(new URI("memory:///")).getClass());
    }

    @Test
    public void createDsTests() throws Exception {
        assertEquals(EmbeddedDatastore.class, provider.createDatastore(unit("memory:///")).getClass());
    }

    private XOUnit unit(String uri) throws Exception {
        return XOUnit.builder().uri(new URI(uri)).build();
    }
}
