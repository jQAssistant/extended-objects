package com.buschmais.xo.neo4j.doc.repository;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

import com.buschmais.xo.api.Query.Result;
import com.buschmais.xo.api.ResultIterable;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.doc.AbstractDocumentationTest;

public class RepositoryTest extends AbstractDocumentationTest {

    @Override
    protected void configure(XOUnit.XOUnitBuilder builder) {
        builder.type(Person.class).type(PersonRepository.class).type(TypedPersonRepository.class);
    }

    @Test
    public void repository() throws URISyntaxException, IOException {
        // tag::Repository[]
        xoManager.currentTransaction().begin();
        Person person = xoManager.create(Person.class);
        person.setName("Indiana Jones");

        PersonRepository personRepository = xoManager.getRepository(PersonRepository.class);
        Result<Person> personsByName = personRepository.getPersonsByName("Indiana Jones");
        Person indy = personsByName.getSingleResult();

        xoManager.currentTransaction().commit();
        // end::Repository[]
    }

    @Test
    public void typedRepository() throws URISyntaxException, IOException {
        xoManager.currentTransaction().begin();
        Person person = xoManager.create(Person.class);
        person.setName("Indiana Jones");

        // tag::TypedRepository[]
        TypedPersonRepository personRepository = xoManager.getRepository(TypedPersonRepository.class);
        ResultIterable<Person> persons = personRepository.find("Indiana Jones");
        Person indy = persons.getSingleResult();
        // end::TypedRepository[]

        xoManager.currentTransaction().commit();
    }

}
