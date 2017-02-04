package com.buschmais.xo.neo4j.embedded.test.inheritance.composite;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Relation;

public interface ArrayValueDescriptor extends ValueDescriptor<List<ObjectDescriptor>>, ArrayDescriptor {

    @Relation("HAS_ELEMENT")
    @Override
    List<ObjectDescriptor> getValue();

    @Override
    void setValue(List<ObjectDescriptor> value);

}
