package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.impl.common.reflection.PropertyMethod;

public class PrimitivePropertyMethodMetadata<DatastoreMetadata> extends AbstractPropertyMethodMetadata<DatastoreMetadata> {

    protected PrimitivePropertyMethodMetadata(PropertyMethod beanPropertyMethod, DatastoreMetadata datastoreMetadata) {
        super(beanPropertyMethod,datastoreMetadata);
    }
}
