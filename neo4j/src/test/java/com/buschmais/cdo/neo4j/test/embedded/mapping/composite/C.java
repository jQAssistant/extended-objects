package com.buschmais.cdo.neo4j.test.embedded.mapping.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label("B")
public interface C extends A, B {
}
