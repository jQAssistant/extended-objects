package com.buschmais.xo.neo4j.test.inheritance.composite;

import java.util.List;

public interface ArrayValueDescriptor extends ValueDescriptor<List<ObjectDescriptor>> {

    @Override
    List<ObjectDescriptor> getValue();

    @Override
    void setValue(List<ObjectDescriptor> value);

}
