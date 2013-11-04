package com.buschmais.cdo.neo4j.test.composite;

import com.buschmais.cdo.neo4j.annotation.Indexed;
import com.buschmais.cdo.neo4j.annotation.Label;
import com.buschmais.cdo.neo4j.annotation.Property;
import com.buschmais.cdo.neo4j.annotation.Relation;

import java.util.Set;

@Label("A")
public interface A extends Version {

    @Indexed
    String getIndex();

    void setIndex(String index);

    String getString();

    void setString(String string);

    B getB();

    void setB(B b);

    Set<B> getSetOfB();

}
