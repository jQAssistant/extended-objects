package com.buschmais.cdo.neo4j.test.mapping.composite;

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

    @Property("MAPPED_STRING")
    String getMappedString();

    void setMappedString(String mapppedString);

    B getB();

    void setB(B b);

    @Relation("MAPPED_B")
    B getMappedB();

    void setMappedB(B mappedB);

    Set<B> getSetOfB();

    @Relation("MAPPED_SET_OF_B")
    Set<B> getMappedSetOfB();

    Enumeration getEnumeratedValue();

    void setEnumeratedValue(Enumeration enumeration);

}
