package com.buschmais.xo.neo4j.doc.transientproperty;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.doc.AbstractDocumentationTest;

public class TransientPropertyTest extends AbstractDocumentationTest {

    @Override
    protected void configure(XOUnit.XOUnitBuilder builder) {
        builder.type(Person.class);
    }

    @Test
    public void transientProperty() throws URISyntaxException, IOException {
        xoManager.currentTransaction().begin();
        Person person = xoManager.create(Person.class);
        person.setName("Indiana Jones");
        xoManager.currentTransaction().commit();
    }

}
