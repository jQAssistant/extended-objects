package com.buschmais.xo.neo4j.test.inheritance.composite;

import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

public interface ArrayValueDescriptor extends ValueDescriptor<List<ObjectDescriptor>>, ArrayDescriptor {

    @Relation("HAS_ELEMENT")
    @Override
    List<ObjectDescriptor> getValue();

    @Override
    void setValue(List<ObjectDescriptor> value);

}
