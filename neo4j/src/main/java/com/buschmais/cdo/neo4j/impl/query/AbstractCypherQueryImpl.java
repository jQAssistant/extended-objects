package com.buschmais.cdo.neo4j.impl.query;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.Query;
import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;

import java.util.*;

public abstract class AbstractCypherQueryImpl<QL> implements Query {

    private QL expression;

    private ExecutionEngine executionEngine;

    private InstanceManager instanceManager;

    private List<Class<?>> types;

    private Map<String, Object> parameters = null;

    public AbstractCypherQueryImpl(QL expression, ExecutionEngine executionEngine, InstanceManager instanceManager, List<Class<?>> types) {
        this.expression = expression;
        this.executionEngine = executionEngine;
        this.instanceManager = instanceManager;
        this.types = types;
    }

    @Override
    public Query withParameter(String name, Object value) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        Object oldValue = parameters.put(name, value);
        if (oldValue != null) {
            throw new CdoException("Parameter '" + name + "' has alread been assigned to value '" + value + "'.");
        }
        return this;
    }

    @Override
    public Query withParameters(Map<String, Object> parameters) {
        if (this.parameters != null) {
            throw new CdoException(("Parameters have already beed assigned: " + parameters));
        }
        this.parameters = parameters;
        return this;
    }

    @Override
    public <T> Result<T> execute() {
        String query = getQuery();
        ExecutionResult result = executionEngine.execute(query, parameters != null ? parameters : Collections.<String, Object>emptyMap());
        List<Class<?>> resultTypes = getResultTypes(expression, types);
        return new IterableQueryResultImpl(instanceManager, result.columns(), result.iterator(), resultTypes);
    }

    protected QL getExpression() {
        return expression;
    }

    protected abstract String getQuery();

    protected abstract List<Class<?>> getResultTypes(QL expression, List<Class<?>> types);

}
