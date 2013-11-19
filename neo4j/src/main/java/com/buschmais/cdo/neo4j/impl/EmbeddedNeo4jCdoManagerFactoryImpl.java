package com.buschmais.cdo.neo4j.impl;

import com.buschmais.cdo.api.IterableResult;
import com.buschmais.cdo.neo4j.impl.node.metadata.NodeMetadata;
import com.buschmais.cdo.neo4j.impl.node.metadata.NodeMetadataProvider;
import com.buschmais.cdo.neo4j.impl.node.metadata.PrimitivePropertyMethodMetadata;
import com.buschmais.cdo.neo4j.impl.query.QueryExecutor;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;

public class EmbeddedNeo4jCdoManagerFactoryImpl extends AbstractNeo4jCdoManagerFactoryImpl<GraphDatabaseService> {


    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNeo4jCdoManagerFactoryImpl.class);

    public EmbeddedNeo4jCdoManagerFactoryImpl(URL url, Class<?>... entities) {
        super(url, entities);
    }

    protected GraphDatabaseService createGraphDatabaseService(URL url) {
        return new GraphDatabaseFactory().newEmbeddedDatabase(url.getPath());
    }

    @Override
    protected QueryExecutor createQueryExecutor(GraphDatabaseService graphDatabaseService) {
        final ExecutionEngine executionEngine = new ExecutionEngine(graphDatabaseService);
        return new QueryExecutor() {
            @Override
            public Iterator<Map<String, Object>> execute(String query, Map<String, Object> parameters) {
                ExecutionResult executionResult = executionEngine.execute(query, parameters);
                return executionResult.iterator();
            }
        };
    }

    @Override
    protected void init(GraphDatabaseService graphDatabaseService, NodeMetadataProvider nodeMetadataProvider) {
        Transaction transaction = graphDatabaseService.beginTx();
        for (NodeMetadata nodeMetadata : nodeMetadataProvider.getRegisteredNodeMetadata()) {
            Label label = nodeMetadata.getLabel();
            PrimitivePropertyMethodMetadata indexedProperty = nodeMetadata.getIndexedProperty();
            if (label != null && indexedProperty != null) {
                IndexDefinition index = null;
                for (IndexDefinition indexDefinition : graphDatabaseService.schema().getIndexes(label)) {
                    for (String s : indexDefinition.getPropertyKeys()) {
                        if (s.equals(indexedProperty.getPropertyName())) {
                            index = indexDefinition;
                        }
                    }
                }
                if (indexedProperty != null && index == null) {
                    LOGGER.info("Creating index for label {} on property '{}'.", label, indexedProperty.getPropertyName());
                    graphDatabaseService.schema().indexFor(label).on(indexedProperty.getPropertyName()).create();
                } else if (indexedProperty == null && index != null) {
                    LOGGER.info("Dropping index for label {} on properties '{}'.", label, index.getPropertyKeys());
                    index.drop();
                }
            }
        }
        transaction.success();
        transaction.close();
    }

}
