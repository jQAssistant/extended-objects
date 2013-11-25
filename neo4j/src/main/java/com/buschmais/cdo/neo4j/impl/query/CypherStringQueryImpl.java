package com.buschmais.cdo.neo4j.impl.query;

import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import com.buschmais.cdo.neo4j.spi.DatastoreSession;

import java.util.List;

public class CypherStringQueryImpl extends AbstractCypherQueryImpl<String> {

    public CypherStringQueryImpl(String expression, DatastoreSession datastoreSession, InstanceManager instanceManager, List<Class<?>> types) {
        super(expression, datastoreSession, instanceManager, types);
    }

    @Override
    protected String getQuery() {
        return getExpression();
    }

    @Override
    protected List<Class<?>> getResultTypes(String expression, List<Class<?>> types) {
        return types;
    }
}
