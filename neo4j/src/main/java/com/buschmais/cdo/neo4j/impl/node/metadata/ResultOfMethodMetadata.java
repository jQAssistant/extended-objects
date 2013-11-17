package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.impl.common.reflection.BeanMethod;

public class ResultOfMethodMetadata extends AbstractMethodMetadata<BeanMethod> {

    private Class<?> query;

    private String usingThisAs;

    public ResultOfMethodMetadata(BeanMethod beanMethod, Class<?> query, String usingThisAs) {
        super(beanMethod);

        this.query = query;
        this.usingThisAs = usingThisAs;
    }

    public Class<?> getQuery() {
        return query;
    }

    public String getUsingThisAs() {
        return usingThisAs;
    }
}
