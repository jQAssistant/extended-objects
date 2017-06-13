package com.buschmais.xo.neo4j.test.generics.composite;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

@Label
public interface ValueContainer<V extends Value> {

    @Relation
    List<V> getValues();

}
