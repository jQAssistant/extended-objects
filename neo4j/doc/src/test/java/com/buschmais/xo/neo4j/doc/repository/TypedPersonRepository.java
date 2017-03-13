package com.buschmais.xo.neo4j.doc.repository;

import com.buschmais.xo.api.annotation.Repository;
import com.buschmais.xo.neo4j.api.TypedNeo4jRepository;

// tag::Class[]
@Repository
public interface TypedPersonRepository extends TypedNeo4jRepository<Person> {
}
// end::Class[]
