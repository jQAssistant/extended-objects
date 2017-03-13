package com.buschmais.xo.neo4j.doc.dynamic;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

// tag::Class[]
@Label
public interface Actor extends Person {

    @ActedIn
    @Outgoing
    List<Movie> getActedIn();

}
// end::Class[]
