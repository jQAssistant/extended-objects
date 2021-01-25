package com.buschmais.xo.spring.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label
public interface Person {

    String getName();

    void setName(String name);
}
