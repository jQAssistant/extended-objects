package com.buschmais.xo.api.metadata.method;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

import com.buschmais.xo.api.metadata.reflection.AnnotatedMethod;

import lombok.Builder;
import lombok.Getter;

public class ResultOfMethodMetadata<DatastoreMetadata> extends AbstractMethodMetadata<AnnotatedMethod, DatastoreMetadata> {

    private final AnnotatedElement query;

    private final Class<?> rowType;

    private final String usingThisAs;

    private final List<QueryParameter> parameters;

    private final boolean singleResult;

    public ResultOfMethodMetadata(AnnotatedMethod annotatedMethod, AnnotatedElement query, Class<?> rowType, String usingThisAs,
        List<QueryParameter> parameters, boolean singleResult) {
        super(annotatedMethod, null);
        this.query = query;
        this.rowType = rowType;
        this.usingThisAs = usingThisAs;
        this.parameters = parameters;
        this.singleResult = singleResult;
    }

    public Class<?> getRowType() {
        return rowType;
    }

    public AnnotatedElement getQuery() {
        return query;
    }

    public String getUsingThisAs() {
        return usingThisAs;
    }

    public List<QueryParameter> getParameters() {
        return parameters;
    }

    public boolean isSingleResult() {
        return singleResult;
    }

    @Builder
    @Getter
    public static class QueryParameter {
        private String name;
    }
}
