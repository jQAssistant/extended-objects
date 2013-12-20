package com.buschmais.cdo.neo4j.test.embedded.demo.composite;

import com.buschmais.cdo.api.annotation.ResultOf;
import com.buschmais.cdo.neo4j.api.annotation.Cypher;
import com.buschmais.cdo.neo4j.api.annotation.Label;

import java.util.List;

import static com.buschmais.cdo.api.annotation.ResultOf.Parameter;

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
