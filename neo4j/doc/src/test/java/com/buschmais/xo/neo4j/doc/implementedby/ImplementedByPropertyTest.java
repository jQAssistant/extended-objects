package com.buschmais.xo.neo4j.doc.implementedby;

import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.doc.AbstractDocumentationTest;

import org.junit.Test;

public class ImplementedByPropertyTest extends AbstractDocumentationTest {

    @Override
    protected void configure(XOUnit.XOUnitBuilder builder) {
        builder.type(Person.class);
    }

    @Test
    public void implementedBy() {
        xoManager.currentTransaction().begin();
        Person person = xoManager.create(Person.class);
        person.setName("Harrison", "Ford");
        xoManager.currentTransaction().commit();
    }

}
