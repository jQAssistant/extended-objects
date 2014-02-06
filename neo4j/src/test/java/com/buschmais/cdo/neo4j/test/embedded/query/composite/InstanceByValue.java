package com.buschmais.cdo.neo4j.test.embedded.query.composite;

import com.buschmais.cdo.neo4j.api.annotation.Cypher;
import com.buschmais.cdo.neo4j.test.embedded.query.composite.A;

@Cypher("match (a:A) where a.value={value} return a")
public interface InstanceByValue {

    A getA();

}
