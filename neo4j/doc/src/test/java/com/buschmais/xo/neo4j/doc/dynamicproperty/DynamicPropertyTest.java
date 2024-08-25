package com.buschmais.xo.neo4j.doc.dynamicproperty;

import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.doc.AbstractDocumentationTest;

import org.junit.Test;

public class DynamicPropertyTest extends AbstractDocumentationTest {

    @Override
    protected void configure(XOUnit.XOUnitBuilder builder) {
        builder.type(Person.class)
            .type(Actor.class)
            .type(Movie.class);
    }

    @Test
    public void dynamicProperty() {
        xoManager.currentTransaction()
            .begin();

        Movie movie = xoManager.create(Movie.class);
        Actor actor = xoManager.create(Actor.class);
        actor.setAge(42);
        movie.getActors()
            .add(actor);

        Long totalActors = movie.getActorCount();
        Long actorsWithAge42 = movie.getActorCountByAge(42);

        xoManager.currentTransaction()
            .commit();
    }

}
