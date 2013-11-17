package com.buschmais.cdo.neo4j.test.query.typedquery;

import com.buschmais.cdo.neo4j.api.annotation.Cypher;
import com.buschmais.cdo.neo4j.test.query.composite.A;

@Cypher("match (a:A) where a.value={value} return a")
public interface InstanceByValue {

    A getA();

}
