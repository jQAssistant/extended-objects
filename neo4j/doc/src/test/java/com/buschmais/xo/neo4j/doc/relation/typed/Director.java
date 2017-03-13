package com.buschmais.xo.neo4j.doc.relation.typed;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.doc.relation.Person;

// tag::Class[]
@Label
public interface Director extends Person {

    List<Directed> getDirected();

}
// end::Class[]
