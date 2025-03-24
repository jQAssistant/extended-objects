package com.buschmais.xo.neo4j.doc.relation.typed;

import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.doc.AbstractDocumentationTest;
import com.buschmais.xo.neo4j.doc.relation.Person;

import org.junit.Test;

public class TypedRelationTest extends AbstractDocumentationTest {

    @Override
    protected void configure(XOUnit.XOUnitBuilder builder) {
        builder.type(Person.class)
            .type(Director.class)
            .type(Movie.class)
            .type(Directed.class);
    }

    @Test
    public void typedRelation() {
        // tag::Create[]
        xoManager.currentTransaction()
            .begin();

        Director director = xoManager.create(Director.class);
        Movie movie = xoManager.create(Movie.class);

        Directed directed = xoManager.create(director, Directed.class, movie);
        directed.setYear(2017);

        xoManager.currentTransaction()
            .commit();
        // end::Create[]
    }

}
