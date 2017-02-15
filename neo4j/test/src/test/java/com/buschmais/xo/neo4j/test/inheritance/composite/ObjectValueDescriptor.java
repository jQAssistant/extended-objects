package com.buschmais.xo.neo4j.test.inheritance.composite;

import com.buschmais.xo.neo4j.api.annotation.Relation;

public interface ObjectValueDescriptor extends ValueDescriptor<ObjectDescriptor>, ObjectDescriptor {

    @Relation("IS")
    @Override
    ObjectDescriptor getValue();

    @Override
    void setValue(ObjectDescriptor value);

}
