package com.buschmais.xo.neo4j.doc.label.explicit;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;

// tag::Class[]
@Label("MyPerson")
public interface Person {

    @Property("myName")
    String getName();

    void setName(String name);

}
// end::Class[]
