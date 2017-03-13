package com.buschmais.xo.neo4j.doc.relation.unidirectional.explicit;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;
import com.buschmais.xo.neo4j.doc.relation.Person;
import com.buschmais.xo.neo4j.doc.relation.unidirectional.Movie;

// tag::Class[]
@Label
public interface Actor extends Person {

    @Relation("ACTED_IN")
    @Outgoing
    List<Movie> getActedIn();

}
// end::Class[]
