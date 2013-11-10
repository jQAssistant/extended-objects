package com.buschmais.cdo.neo4j.test.mapping.composite;

import com.buschmais.cdo.neo4j.annotation.Label;

@Label("C")
public interface C extends A, B {
}
