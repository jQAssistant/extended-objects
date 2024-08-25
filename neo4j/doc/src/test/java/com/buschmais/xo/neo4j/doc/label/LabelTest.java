package com.buschmais.xo.neo4j.doc.label;

import java.net.URISyntaxException;

import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.doc.AbstractDocumentationTest;

import org.junit.Test;

public class LabelTest extends AbstractDocumentationTest {

    @Override
    protected void configure(XOUnit.XOUnitBuilder builder) {
        builder.type(Person.class)
            .type(Actor.class);
    }

    @Test
    public void label() throws URISyntaxException {
        xoManager.currentTransaction()
            .begin();
        xoManager.create(Person.class);
        xoManager.currentTransaction()
            .commit();
    }

}
