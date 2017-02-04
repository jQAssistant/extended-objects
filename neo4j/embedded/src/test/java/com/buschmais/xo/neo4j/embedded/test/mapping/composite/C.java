package com.buschmais.xo.neo4j.embedded.test.mapping.composite;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("B")
public interface C extends A, B {
}
