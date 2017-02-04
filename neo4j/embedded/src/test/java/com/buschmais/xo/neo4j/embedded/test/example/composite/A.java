package com.buschmais.xo.neo4j.embedded.test.example.composite;

import static com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import static com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;

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
