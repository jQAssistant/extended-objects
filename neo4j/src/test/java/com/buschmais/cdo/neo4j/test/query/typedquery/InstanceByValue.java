package com.buschmais.cdo.neo4j.test.query.typedquery;

import com.buschmais.cdo.neo4j.api.annotation.Cypher;
import com.buschmais.cdo.neo4j.test.query.composite.A;

@Cypher("match (a:A) where a.Value={value} return a as A")
public interface InstanceByValue {

    A getA();
}
