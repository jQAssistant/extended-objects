package com.buschmais.cdo.neo4j.test.mapping.composite;

import com.buschmais.cdo.neo4j.annotation.Label;

@Label(value = "D", usingIndexOf = A.class)
public interface D extends A {
}
