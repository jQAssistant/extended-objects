package com.buschmais.cdo.neo4j.test.query.composite;

import com.buschmais.cdo.neo4j.api.annotation.Cypher;

@Cypher("match (a:A) where a.value={value} return a")
public interface TypedQuery {

    A getA();
}
