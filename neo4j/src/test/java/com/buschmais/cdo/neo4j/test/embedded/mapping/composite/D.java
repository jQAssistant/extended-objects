package com.buschmais.cdo.neo4j.test.embedded.mapping.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label(value = "D", usingIndexedPropertyOf = A.class)
public interface D extends A {
}
