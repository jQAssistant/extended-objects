package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.impl.common.reflection.BeanMethod;

public abstract class AbstractMethodMetadata<B extends BeanMethod, DatastoreMetadata> {

    private B beanMethod;

    private DatastoreMetadata datastoreMetadata;

    protected AbstractMethodMetadata(B beanMethod) {
        this.beanMethod = beanMethod;
    }

    public B getBeanMethod() {
        return beanMethod;
    }

    public DatastoreMetadata getDatastoreMetadata() {
        return datastoreMetadata;
    }

    @Override
    public String toString() {
        return "AbstractMethodMetadata{" + "beanMethod=" + beanMethod + '}';
    }
}
