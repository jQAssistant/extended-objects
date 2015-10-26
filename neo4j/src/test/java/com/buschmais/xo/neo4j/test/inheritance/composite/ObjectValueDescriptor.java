package com.buschmais.xo.neo4j.test.inheritance.composite;

public interface ObjectValueDescriptor extends ValueDescriptor<ObjectDescriptor> {

    @Override
    ObjectDescriptor getValue();

    @Override
    void setValue(ObjectDescriptor value);

}
