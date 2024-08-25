package com.buschmais.xo.neo4j.doc.relation.qualified;

import java.net.URISyntaxException;

import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.doc.AbstractDocumentationTest;
import com.buschmais.xo.neo4j.doc.relation.Person;

import org.junit.Test;

public class QualifiedRelationTest extends AbstractDocumentationTest {

    @Override
    protected void configure(XOUnit.XOUnitBuilder builder) {
        builder.type(Person.class)
            .type(Actor.class)
            .type(Movie.class);
    }

    @Test
    public void qualifiedRelation() throws URISyntaxException {
        xoManager.currentTransaction()
            .begin();
        Actor actor = xoManager.create(Actor.class);
        Movie movie = xoManager.create(Movie.class);
        actor.getActedIn()
            .add(movie);
        xoManager.currentTransaction()
            .commit();
    }

}
