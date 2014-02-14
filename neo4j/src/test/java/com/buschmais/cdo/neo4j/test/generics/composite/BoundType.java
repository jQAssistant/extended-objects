package com.buschmais.cdo.neo4j.test.generics.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label("Bound")
public interface BoundType extends GenericSuperType<String> {
}
