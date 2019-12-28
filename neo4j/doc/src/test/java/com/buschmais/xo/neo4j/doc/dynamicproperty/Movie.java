package com.buschmais.xo.neo4j.doc.dynamicproperty;

import java.util.List;

import com.buschmais.xo.api.annotation.ResultOf;
import com.buschmais.xo.api.annotation.ResultOf.Parameter;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;

// tag::Class[]
@Label
public interface Movie {

    @ResultOf
    @Cypher("match (a:Actor)-[:ACTED_IN]->(m:Movie) where id(m)={this} return count(a)")
    Long getActorCount();

    @ResultOf
    @Cypher("match (a:Actor)-[:ACTED_IN]->(m:Movie) where id(m)={this} and a.age={age} return count(a)")
    Long getActorCountByAge(@Parameter("age") int age);

    @ActedIn
    @Incoming
    List<Actor> getActors();

}
// end::Class[]
