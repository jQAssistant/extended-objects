package com.buschmais.xo.neo4j.doc.repository;

import com.buschmais.xo.api.Query.Result;
import com.buschmais.xo.api.annotation.Repository;
import com.buschmais.xo.api.annotation.ResultOf;
import com.buschmais.xo.api.annotation.ResultOf.Parameter;
import com.buschmais.xo.neo4j.api.annotation.Cypher;

// tag::Class[]
@Repository
public interface PersonRepository {

    @ResultOf
    @Cypher("MATCH (p:Person) WHERE p.name=$name RETURN p")
    Result<Person> getPersonsByName(@Parameter("name") String name);
}
// end::Class[]
