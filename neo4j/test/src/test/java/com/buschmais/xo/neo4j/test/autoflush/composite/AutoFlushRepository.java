package com.buschmais.xo.neo4j.test.autoflush.composite;

import com.buschmais.xo.api.annotation.AutoFlush;
import com.buschmais.xo.api.annotation.Repository;
import com.buschmais.xo.api.annotation.ResultOf;
import com.buschmais.xo.api.annotation.ResultOf.Parameter;
import com.buschmais.xo.neo4j.api.annotation.Cypher;

@Repository
public interface AutoFlushRepository {

    @ResultOf
    @Cypher("MATCH (a:A) WHERE a.name={name} RETURN a")
    @AutoFlush(false)
    A findByName(@Parameter("name") String name);

}
