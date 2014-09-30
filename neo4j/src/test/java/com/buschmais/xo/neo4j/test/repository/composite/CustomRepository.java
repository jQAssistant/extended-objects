package com.buschmais.xo.neo4j.test.repository.composite;

import com.buschmais.xo.api.annotation.Repository;
import com.buschmais.xo.api.annotation.ResultOf;
import com.buschmais.xo.neo4j.api.annotation.Cypher;

import static com.buschmais.xo.api.annotation.ResultOf.Parameter;

@Repository
public interface CustomRepository {

    @ResultOf
    @Cypher("match a where a.name={name} return a")
    A findByName(@Parameter("name") String name);

}
