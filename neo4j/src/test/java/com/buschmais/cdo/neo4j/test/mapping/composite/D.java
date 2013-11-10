package com.buschmais.cdo.neo4j.test.mapping.composite;

import com.buschmais.cdo.neo4j.annotation.Label;
import com.buschmais.cdo.neo4j.annotation.UsingIndexOf;

@Label("D") @UsingIndexOf(A.class)
public interface D extends A{
}
