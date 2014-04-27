package com.buschmais.xo.neo4j.impl.datastore.query;

import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;

import com.buschmais.xo.api.NativeQueryEngine;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.neo4j.api.query.CypherQuery;

public class EmbeddedCypherQueryEngine implements NativeQueryEngine<CypherQuery> {

    private final ExecutionEngine executionEngine;

    public EmbeddedCypherQueryEngine(final GraphDatabaseService graphDatabaseService) {
        executionEngine = new ExecutionEngine(graphDatabaseService);
    }

    @Override
    public ResultIterator<Map<String, Object>> execute(final CypherQuery query, final Map<String, Object> parameters) {
        final ExecutionResult executionResult = executionEngine.execute(query.getExpression(), parameters);
        return new ResourceResultIterator(executionResult.iterator());
    }

}
