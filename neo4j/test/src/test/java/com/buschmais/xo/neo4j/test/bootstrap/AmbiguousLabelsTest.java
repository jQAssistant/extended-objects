package com.buschmais.xo.neo4j.test.bootstrap;

import java.net.URI;
import java.net.URISyntaxException;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.embedded.api.EmbeddedNeo4jXOProvider;
import com.buschmais.xo.neo4j.test.bootstrap.composite.A;
import com.buschmais.xo.neo4j.test.bootstrap.composite.AmbiguousA;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class AmbiguousLabelsTest {

    @Test
    public void strict() throws URISyntaxException {
        try (XOManagerFactory xoManagerFactory = createFactory(XOUnit.MappingConfiguration.builder()
            .strictValidation(true)
            .build())) {
            fail("Expecting an " + XOException.class.getName());
        } catch (XOException e) {
            assertThat(e.getMessage()).contains("AmbiguousA");
        }
    }

    @Test
    public void warn() throws URISyntaxException {
        try (XOManagerFactory xoManagerFactory = createFactory(XOUnit.MappingConfiguration.builder()
            .strictValidation(false)
            .build())) {
            XOManager xoManager = xoManagerFactory.createXOManager();
            assertThat(xoManager).isNotNull();
            xoManager.close();
        }
    }

    @Test
    public void defaultSetting() throws URISyntaxException {
        try (XOManagerFactory xoManagerFactory = createFactory(null)) {
            XOManager xoManager = xoManagerFactory.createXOManager();
            assertThat(xoManager).isNotNull();
            xoManager.close();
        }
    }

    private XOManagerFactory createFactory(XOUnit.MappingConfiguration mappingConfiguration) throws URISyntaxException {
        XOUnit.XOUnitBuilder builder = XOUnit.builder()
            .provider(EmbeddedNeo4jXOProvider.class)
            .uri(new URI("memory:///"))
            .type(A.class)
            .type(AmbiguousA.class);
        if (mappingConfiguration != null) {
            builder.mappingConfiguration(mappingConfiguration);
        }
        XOUnit xoUnit = builder.build();
        return XO.createXOManagerFactory(xoUnit);
    }

}
