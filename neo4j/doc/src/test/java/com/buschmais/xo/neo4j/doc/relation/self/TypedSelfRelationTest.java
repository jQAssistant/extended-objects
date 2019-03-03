package com.buschmais.xo.neo4j.doc.relation.self;

import java.net.URISyntaxException;

import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.doc.AbstractDocumentationTest;

import org.junit.Test;

public class TypedSelfRelationTest extends AbstractDocumentationTest {

    @Override
    protected void configure(XOUnit.XOUnitBuilder builder) {
        builder.type(Movie.class).type(References.class);
    }

    @Test
    public void typedSelfRelation() throws URISyntaxException {
        // tag::Create[]
        xoManager.currentTransaction().begin();
        Movie movie1 = xoManager.create(Movie.class);
        Movie movie2 = xoManager.create(Movie.class);

        References references = xoManager.create(movie1, References.class, movie2);
        references.setMinute(42);
        references.setSecond(42);

        xoManager.currentTransaction().commit();
        // end::Create[]
    }

}
