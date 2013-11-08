package com.buschmais.cdo.neo4j.test.composite.ambiguous;

import com.buschmais.cdo.neo4j.annotation.Indexed;
import com.buschmais.cdo.neo4j.annotation.Label;

@Label("A")
public interface A {

    @Indexed
    String getIndex();

    void setIndex(String index);

    Enumeration getEnumeration();

    void setEnumeration(Enumeration enumeration);

}
