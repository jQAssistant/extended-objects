package com.buschmais.xo.neo4j.test.findbyid.composite;

import com.buschmais.xo.neo4j.api.annotation.Batchable;
import com.buschmais.xo.neo4j.api.annotation.Label;

@Label
@Batchable(false)
public interface A {

    A2B getA2B();

}
