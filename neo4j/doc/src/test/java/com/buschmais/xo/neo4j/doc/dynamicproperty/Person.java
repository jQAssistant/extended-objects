package com.buschmais.xo.neo4j.doc.dynamic;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label
public interface Person {

    int getAge();

    void setAge(int age);

}
