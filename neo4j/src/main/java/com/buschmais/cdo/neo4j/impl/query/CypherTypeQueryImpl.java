package com.buschmais.cdo.neo4j.impl.query;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.neo4j.api.annotation.Cypher;
import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import com.buschmais.cdo.neo4j.spi.DatastoreSession;
import com.buschmais.cdo.neo4j.spi.TypeSet;

import java.util.Collection;

public class CypherTypeQueryImpl extends AbstractCypherQueryImpl<Class<?>> {

    public CypherTypeQueryImpl(Class<?> expression, DatastoreSession datastoreSession, InstanceManager instanceManager, Collection<Class<?>> types) {
        super(expression, datastoreSession, instanceManager, types);
    }

    @Override
    protected String getQuery() {
        Class<?> expression = getExpression();
        Cypher cypher = expression.getAnnotation(Cypher.class);
        if (cypher == null) {
            throw new CdoException("Type '" + expression.getName() + "' is not annotated with '" + Cypher.class.getName() + "'");
        }
        return cypher.value();
    }

    @Override
    protected TypeSet getResultTypes(Class<?> expression, Collection<Class<?>> types) {
        TypeSet resultTypes = new TypeSet();
        resultTypes.addAll(types);
        resultTypes.add(expression);
        return resultTypes;
    }
}
