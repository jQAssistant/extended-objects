package com.buschmais.cdo.neo4j.test.mapping.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label("C")
public interface C extends A, B {
}
