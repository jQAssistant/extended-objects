package com.buschmais.xo.neo4j.doc.bootstrap;

import com.buschmais.xo.neo4j.api.annotation.Label;

// tag::Class[]
@Label
public interface Person {

    String getName();

    void setName(String name);

}
// end::Class[]
