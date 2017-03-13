package com.buschmais.xo.neo4j.doc.transientproperty;

import com.buschmais.xo.api.annotation.Transient;
import com.buschmais.xo.neo4j.api.annotation.Label;

// tag::Class[]
@Label
public interface Person {

    @Transient
    String getName();
    void setName(String name);

}
// end::Class[]
