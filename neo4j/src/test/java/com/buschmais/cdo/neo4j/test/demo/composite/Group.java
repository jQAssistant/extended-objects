package com.buschmais.cdo.neo4j.test.demo.composite;

import com.buschmais.cdo.neo4j.api.annotation.Cypher;
import com.buschmais.cdo.neo4j.api.annotation.Label;
import com.buschmais.cdo.neo4j.api.annotation.Relation;
import com.buschmais.cdo.neo4j.api.annotation.ResultOf;

import java.util.List;

import static com.buschmais.cdo.neo4j.api.annotation.ResultOf.Parameter;

@Label("Group")
public interface Group {

    @Relation("HasMember")
    List<Person> getMembers();

    @ResultOf
    MemberByName getMemberByName(@Parameter("name") String name);

    @Cypher("match (g:Group)-[:HasMember]->(p:Person) where g={this} and p.name={name} return p as member")
    public interface MemberByName {
        Person getMember();
    }

}
