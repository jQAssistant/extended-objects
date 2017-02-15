package com.buschmais.xo.neo4j.test.inheritance.composite;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

@Label("Object")
public interface ObjectDescriptor {

    @Relation("HAS_VALUE")
    List<ValueDescriptor<?>> getValues();

}
