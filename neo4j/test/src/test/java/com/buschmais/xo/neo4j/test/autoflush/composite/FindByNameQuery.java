package com.buschmais.xo.neo4j.test.autoflush.composite;

import com.buschmais.xo.api.annotation.AutoFlush;
import com.buschmais.xo.neo4j.api.annotation.Cypher;

@Cypher("MATCH (a:A) WHERE a.name={name} RETURN a")
@AutoFlush(false)
public interface FindByNameQuery {

    A getA();

}
