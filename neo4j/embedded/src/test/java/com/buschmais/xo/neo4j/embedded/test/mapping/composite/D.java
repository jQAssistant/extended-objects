package com.buschmais.xo.neo4j.embedded.test.mapping.composite;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label(value = "D", usingIndexedPropertyOf = A.class)
public interface D extends A {
}
