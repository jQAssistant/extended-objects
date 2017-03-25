package com.buschmais.xo.neo4j.test.flush.composite;

import com.buschmais.xo.api.annotation.Flush;
import com.buschmais.xo.neo4j.api.annotation.Cypher;

@Cypher("MATCH (a:A) WHERE a.name={name} RETURN a")
@Flush(false)
public interface FindByNameQuery {

    A getA();

}
