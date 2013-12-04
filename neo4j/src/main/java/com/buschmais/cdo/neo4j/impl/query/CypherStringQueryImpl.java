package com.buschmais.cdo.neo4j.impl.query;

import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import com.buschmais.cdo.neo4j.spi.DatastoreSession;
import com.buschmais.cdo.neo4j.spi.TypeSet;

import java.util.Collection;

public class CypherStringQueryImpl extends AbstractCypherQueryImpl<String> {

    public CypherStringQueryImpl(String expression, DatastoreSession datastoreSession, InstanceManager instanceManager, Collection<Class<?>> types) {
        super(expression, datastoreSession, instanceManager, types);
    }

    @Override
    protected String getQuery() {
        return getExpression();
    }

    @Override
    protected TypeSet getResultTypes(String expression, Collection<Class<?>> types) {
        TypeSet resultTypes = new TypeSet();
        resultTypes.addAll(types);
        return resultTypes;
    }
}
