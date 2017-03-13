package com.buschmais.xo.neo4j.doc;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.embedded.api.EmbeddedNeo4jXOProvider;

public abstract class AbstractDocumentationTest {

    protected XOManagerFactory xoManagerFactory;

    protected XOManager xoManager;

    @Before
    public void setUp() throws URISyntaxException {
        XOUnit.XOUnitBuilder builder = XOUnit.builder().provider(EmbeddedNeo4jXOProvider.class).uri(new URI("memory:///"));
        builder.mappingConfiguration(XOUnit.MappingConfiguration.builder().strictValidation(true).build());
        configure(builder);
        XOUnit xoUnit = builder.build();
        xoManagerFactory = XO.createXOManagerFactory(xoUnit);
        xoManager = xoManagerFactory.createXOManager();
    }

    @After
    public void tearDown() {
        if (xoManager != null) {
            xoManager.close();
        }
        if (xoManagerFactory != null) {
            xoManagerFactory.close();
        }
    }

    protected abstract void configure(XOUnit.XOUnitBuilder builder);

}
