package com.buschmais.xo.neo4j.doc.relation.unidirectional;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.doc.relation.Person;

// tag::Class[]
@Label
public interface Actor extends Person {

    List<Movie> getActedIn();

}
// end::Class[]
