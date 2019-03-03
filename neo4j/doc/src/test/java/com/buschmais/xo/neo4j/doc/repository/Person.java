package com.buschmais.xo.neo4j.doc.repository;

import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;

// tag::Class[]
@Label
public interface Person {

    @Indexed
    String getName();

    void setName(String name);

}
// end::Class[]
