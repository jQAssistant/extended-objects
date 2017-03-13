package com.buschmais.xo.neo4j.doc.relation.typed;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;

// tag::Class[]
@Label
public interface Movie {

    String getTitle();
    void setTitle();

    List<Directed> getDirected();

}
// end::Class[]
