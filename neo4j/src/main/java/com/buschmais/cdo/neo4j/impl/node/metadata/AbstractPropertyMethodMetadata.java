package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.impl.common.reflection.PropertyMethod;

public abstract class AbstractPropertyMethodMetadata<DatastoreMetadata> extends AbstractMethodMetadata<PropertyMethod, DatastoreMetadata> {

    protected AbstractPropertyMethodMetadata(PropertyMethod beanMethod, DatastoreMetadata datastoreMetadata) {
        super(beanMethod, datastoreMetadata);
    }

}
