package com.buschmais.xo.neo4j.remote;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;

@Label
public interface Person extends CompositeObject {

    @Indexed
    String getName();

    void setName(String name);

}
