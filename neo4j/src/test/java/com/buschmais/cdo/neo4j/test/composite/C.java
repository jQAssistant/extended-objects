package com.buschmais.cdo.neo4j.test.composite;

import com.buschmais.cdo.neo4j.annotation.Label;

@Label("C")
public interface C extends A, B {
}
