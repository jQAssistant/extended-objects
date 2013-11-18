package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.api.annotation.ResultOf;
import com.buschmais.cdo.neo4j.impl.common.reflection.BeanMethod;

import java.util.List;

public class ResultOfMethodMetadata extends AbstractMethodMetadata<BeanMethod> {

    private Class<?> query;

    private String usingThisAs;

    private List<ResultOf.Parameter> parameters;

    public ResultOfMethodMetadata(BeanMethod beanMethod, Class<?> query, String usingThisAs, List<ResultOf.Parameter> parameters) {
        super(beanMethod);

        this.query = query;
        this.usingThisAs = usingThisAs;
        this.parameters = parameters;
    }

    public Class<?> getQuery() {
        return query;
    }

    public String getUsingThisAs() {
        return usingThisAs;
    }

    public List<ResultOf.Parameter> getParameters() {
        return parameters;
    }
}
