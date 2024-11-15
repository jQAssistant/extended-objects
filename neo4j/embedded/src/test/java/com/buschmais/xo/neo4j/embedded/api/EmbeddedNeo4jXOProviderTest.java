package com.buschmais.xo.neo4j.embedded.api;

import java.io.File;
import java.net.URI;
import java.util.List;

import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.embedded.impl.datastore.EmbeddedDatastore;

import org.junit.Test;
import org.neo4j.configuration.GraphDatabaseSettings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class EmbeddedNeo4jXOProviderTest {

    private final EmbeddedNeo4jXOProvider provider = new EmbeddedNeo4jXOProvider();

    @Test
    public void lookupTests() throws Exception {
        assertEquals(FileDatabaseManagementServiceFactory.class, provider.lookupFactory(new File("target/foo").getAbsoluteFile()
                .toURI())
            .getClass());
        assertEquals(MemoryDatabaseManagementServiceFactory.class, provider.lookupFactory(new URI("memory:/foo"))
            .getClass());
    }

    @Test
    public void createDsTests() throws Exception {
        String uri = "memory:" + new File("target/foo").getAbsolutePath();
        assertEquals(EmbeddedDatastore.class, provider.createDatastore(XOUnit.builder()
                .uri(new URI(uri))
                .build())
            .getClass());
    }

    @Test
    public void propertiesBuilder() {
        assertThat(EmbeddedNeo4jXOProvider.propertiesBuilder()
            .property("db.logs.query.enabled", "INFO")
            .build()).containsEntry("neo4j.db.logs.query.enabled", "INFO");
        assertThat(EmbeddedNeo4jXOProvider.propertiesBuilder()
            .property(GraphDatabaseSettings.log_queries, GraphDatabaseSettings.LogQueryLevel.INFO)
            .build()).containsEntry("neo4j.db.logs.query.enabled", "INFO");
        assertThat(EmbeddedNeo4jXOProvider.propertiesBuilder()
            .property(GraphDatabaseSettings.procedure_unrestricted, List.of("graph.*", "apoc.*"))
            .build()).containsEntry("neo4j.dbms.security.procedures.unrestricted", "graph.*,apoc.*");
    }

}
