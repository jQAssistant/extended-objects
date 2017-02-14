package com.buschmais.xo.neo4j.remote;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import org.neo4j.cypher.internal.compiler.v2_3.commands.expressions.Add;

import java.util.List;

@Label
public interface Person extends CompositeObject {

    @Indexed
    String getName();

    void setName(String name);

    @Relation("HAS_ADDRESS")
    List<Address> getAddresses();
}
