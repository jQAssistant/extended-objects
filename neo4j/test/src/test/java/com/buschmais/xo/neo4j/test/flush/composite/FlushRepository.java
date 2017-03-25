package com.buschmais.xo.neo4j.test.flush.composite;

import com.buschmais.xo.api.annotation.Flush;
import com.buschmais.xo.api.annotation.Repository;
import com.buschmais.xo.api.annotation.ResultOf;
import com.buschmais.xo.api.annotation.ResultOf.Parameter;
import com.buschmais.xo.neo4j.api.annotation.Cypher;

@Repository
public interface FlushRepository {

    @ResultOf
    @Cypher("MATCH (a:A) WHERE a.name={name} RETURN a")
    @Flush(false)
    A findByName(@Parameter("name") String name);

}
