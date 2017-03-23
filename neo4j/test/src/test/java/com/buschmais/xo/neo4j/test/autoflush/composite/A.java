package com.buschmais.xo.neo4j.test.autoflush.composite;

import com.buschmais.xo.api.annotation.AutoFlush;
import com.buschmais.xo.api.annotation.ResultOf;
import com.buschmais.xo.api.annotation.ResultOf.Parameter;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.api.annotation.Label;

@Label
public interface A {

    String getName();
    void setName(String name);

    @ResultOf
    @Cypher("MATCH (a) WHERE a.name={name} RETURN a")
    @AutoFlush(false)
    A findByName(@Parameter("name") String name);

}
