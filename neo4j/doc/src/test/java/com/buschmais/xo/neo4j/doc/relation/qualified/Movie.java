package com.buschmais.xo.neo4j.doc.relation.qualified;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;

// tag::Class[]
@Label
public interface Movie {

    String getTitle();

    void setTitle();

    @ActedIn
    @Incoming
    List<Actor> getActors();
}
// end::Class[]
