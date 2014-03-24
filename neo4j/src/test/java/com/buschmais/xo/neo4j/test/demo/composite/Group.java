package com.buschmais.xo.neo4j.test.demo.composite;

import com.buschmais.xo.api.annotation.ResultOf;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.api.annotation.Label;

import java.util.List;

import static com.buschmais.xo.api.annotation.ResultOf.Parameter;

@Label("Group")
public interface Group {

    List<Person> getMembers();

    @ResultOf
    MemberByName getMemberByName(@Parameter("name") String name);

    @Cypher("match (g:Group)-[:Members]->(p:Person) where g={this} and p.name={name} return p as member")
    public interface MemberByName {
        Person getMember();
    }

}
