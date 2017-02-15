package com.buschmais.xo.neo4j.remote;

import com.buschmais.xo.api.annotation.Repository;
import com.buschmais.xo.neo4j.api.TypedNeo4jRepository;

@Repository
public interface PersonRepository extends TypedNeo4jRepository<Person> {
}
