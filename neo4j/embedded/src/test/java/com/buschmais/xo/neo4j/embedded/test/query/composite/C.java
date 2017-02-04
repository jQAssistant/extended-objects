package com.buschmais.xo.neo4j.embedded.test.query.composite;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label(value = "C", usingIndexedPropertyOf = A.class)
public interface C extends A {
}
