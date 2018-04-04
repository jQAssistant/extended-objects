package com.buschmais.xo.neo4j.test.bootstrap;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.embedded.api.EmbeddedNeo4jXOProvider;
import com.buschmais.xo.neo4j.test.bootstrap.composite.A;
import com.buschmais.xo.neo4j.test.bootstrap.composite.AmbiguousA;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class AmbiguousLabelsTest {

    private static GraphDatabaseService graphDatabaseService;

    @BeforeClass
    public static void setUp() {
        graphDatabaseService = new TestGraphDatabaseFactory().newImpermanentDatabase();
    }

    @Test
    public void strict() throws URISyntaxException {
        try (XOManagerFactory xoManagerFactory = createFactory(XOUnit.MappingConfiguration.builder().strictValidation(true).build())) {
            fail("Expecting an " + XOException.class.getName());
        } catch (XOException e) {
            assertThat(e.getMessage(), containsString("AmbiguousA"));
        }
    }

    @Test
    public void warn() throws URISyntaxException {
        try (XOManagerFactory xoManagerFactory = createFactory(XOUnit.MappingConfiguration.builder().strictValidation(false).build())) {
            xoManagerFactory.createXOManager().close();
        }
    }

    @Test
    public void defaultSetting() throws URISyntaxException {
        try (XOManagerFactory xoManagerFactory = createFactory(null)) {
            xoManagerFactory.createXOManager().close();
        }
    }

    private XOManagerFactory createFactory(XOUnit.MappingConfiguration mappingConfiguration) throws URISyntaxException {
        Properties properties = new Properties();
        properties.put(GraphDatabaseService.class.getName(), graphDatabaseService);
        XOUnit.XOUnitBuilder builder = XOUnit.builder().provider(EmbeddedNeo4jXOProvider.class).uri(new URI("graphDb:///")).properties(properties).type(A.class)
                .type(AmbiguousA.class);
        if (mappingConfiguration != null) {
            builder.mappingConfiguration(mappingConfiguration);
        }
        XOUnit xoUnit = builder.build();
        return XO.createXOManagerFactory(xoUnit);
    }

}
