package com.buschmais.xo.api.metadata.method;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

import com.buschmais.xo.api.metadata.reflection.AnnotatedMethod;

import lombok.Builder;
import lombok.Getter;

public class ResultOfMethodMetadata<DatastoreMetadata> extends AbstractMethodMetadata<AnnotatedMethod, DatastoreMetadata> {

    private final AnnotatedElement query;

    private final Class<?> returnType;

    private final Class<?> rowType;

    private final String usingThisAs;

    private final List<QueryParameter> parameters;

    public ResultOfMethodMetadata(AnnotatedMethod annotatedMethod, AnnotatedElement query, Class<?> returnType, Class<?> rowType, String usingThisAs,
        List<QueryParameter> parameters) {
        super(annotatedMethod, null);
        this.query = query;
        this.returnType = returnType;
        this.rowType = rowType;
        this.usingThisAs = usingThisAs;
        this.parameters = parameters;
    }

    public Class<?> getReturnType() {
        return returnType;
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

    @Builder
    @Getter
    public static class QueryParameter {
        private String name;
    }
}
