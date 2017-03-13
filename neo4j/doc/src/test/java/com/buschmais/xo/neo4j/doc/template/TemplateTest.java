package com.buschmais.xo.neo4j.doc.template;

import java.net.URISyntaxException;

import org.junit.Test;

import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.doc.AbstractDocumentationTest;

public class TemplateTest extends AbstractDocumentationTest {

    @Override
    protected void configure(XOUnit.XOUnitBuilder builder) {
        builder.type(Person.class);
    }

    @Test
    public void template() throws URISyntaxException {
        xoManager.currentTransaction().begin();
        xoManager.create(Person.class);
        xoManager.currentTransaction().commit();
    }

}
