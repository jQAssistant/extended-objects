package com.buschmais.xo.neo4j.doc.relation.qualified;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;
import com.buschmais.xo.neo4j.doc.relation.Person;

// tag::Class[]
@Label
public interface Actor extends Person {

    @ActedIn
    @Outgoing
    List<Movie> getActedIn();

}
// end::Class[]
