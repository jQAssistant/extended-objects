package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.api.annotation.ResultOf;
import com.buschmais.cdo.spi.reflection.AnnotatedMethod;

import java.util.List;

public class ResultOfMethodMetadata<DatastoreMetadata> extends AbstractMethodMetadata<AnnotatedMethod, DatastoreMetadata> {

    private Class<?> query;

    private String usingThisAs;

    private List<ResultOf.Parameter> parameters;

    private boolean singleResult;

    public ResultOfMethodMetadata(AnnotatedMethod annotatedMethod, Class<?> query, String usingThisAs, List<ResultOf.Parameter> parameters, boolean singleResult) {
        super(annotatedMethod, null);

        this.query = query;
        this.usingThisAs = usingThisAs;
        this.parameters = parameters;
        this.singleResult = singleResult;
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

    public boolean isSingleResult() {
        return singleResult;
    }
}
