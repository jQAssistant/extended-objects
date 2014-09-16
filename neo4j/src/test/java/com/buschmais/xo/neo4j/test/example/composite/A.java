package com.buschmais.xo.neo4j.test.example.composite;

import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;

import java.util.List;

import static com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import static com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

@Label
public interface A extends Named {

    @Indexed
    String getValue();

    void setValue(String value);

    @Incoming
    Parent getParent();

    @Outgoing
    List<Parent> getChildren();
}
