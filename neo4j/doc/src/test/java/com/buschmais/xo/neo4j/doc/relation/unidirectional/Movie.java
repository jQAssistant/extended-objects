package com.buschmais.xo.neo4j.doc.relation.unidirectional;

import com.buschmais.xo.neo4j.api.annotation.Label;

// tag::Class[]
@Label
public interface Movie {

    String getTitle();

    void setTitle();

}
// end::Class[]
