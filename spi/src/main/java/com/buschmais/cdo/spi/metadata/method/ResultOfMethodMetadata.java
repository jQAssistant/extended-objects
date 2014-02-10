package com.buschmais.cdo.spi.metadata.method;

import com.buschmais.cdo.api.annotation.ResultOf;
import com.buschmais.cdo.spi.reflection.AnnotatedMethod;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

public class ResultOfMethodMetadata<DatastoreMetadata> extends AbstractMethodMetadata<AnnotatedMethod, DatastoreMetadata> {

    private final AnnotatedElement query;

    private final Class<?> returnType;

    private final String usingThisAs;

    private final List<ResultOf.Parameter> parameters;

    private final boolean singleResult;

    public ResultOfMethodMetadata(AnnotatedMethod annotatedMethod, AnnotatedElement query, Class<?> returnType, String usingThisAs, List<ResultOf.Parameter> parameters, boolean singleResult) {
        super(annotatedMethod, null);
        this.query = query;
        this.returnType = returnType;
        this.usingThisAs = usingThisAs;
        this.parameters = parameters;
        this.singleResult = singleResult;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public AnnotatedElement getQuery() {
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
