package com.buschmais.xo.neo4j.doc.implementedby;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.doc.AbstractDocumentationTest;

public class ImplementedByPropertyTest extends AbstractDocumentationTest {

    @Override
    protected void configure(XOUnit.XOUnitBuilder builder) {
        builder.type(Person.class);
    }

    @Test
    public void implementedBy() throws URISyntaxException, IOException {
        xoManager.currentTransaction().begin();
        Person person = xoManager.create(Person.class);
        person.setName("Harrison", "Ford");
        xoManager.currentTransaction().commit();
    }

}
