package com.buschmais.cdo.neo4j.test.embedded.mapping.composite;

import com.buschmais.cdo.neo4j.api.annotation.Cypher;

@Cypher("match (e:E)-[:RELATED_TO]-(f:F) where e={e} and f.value={value} return f")
public interface ByValue {

    F getF();

}
