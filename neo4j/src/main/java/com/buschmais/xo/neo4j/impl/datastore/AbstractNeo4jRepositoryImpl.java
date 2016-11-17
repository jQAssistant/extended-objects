package com.buschmais.xo.neo4j.impl.datastore;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;

import com.buschmais.xo.api.ResultIterable;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.neo4j.api.model.Neo4jLabel;
import com.buschmais.xo.neo4j.api.model.Neo4jNode;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationship;
import com.buschmais.xo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.RelationshipType;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;
import com.buschmais.xo.spi.session.XOSession;

/**
 * Abstract base implementation for Neo4j repositories.
 */
abstract class AbstractNeo4jRepositoryImpl {

    private final GraphDatabaseService graphDatabaseService;
    private final XOSession<Long, Neo4jNode, NodeMetadata, Neo4jLabel, Long, Neo4jRelationship, RelationshipMetadata, RelationshipType, PropertyMetadata> xoSession;

    protected AbstractNeo4jRepositoryImpl(GraphDatabaseService graphDatabaseService,
            XOSession<Long, Neo4jNode, NodeMetadata, Neo4jLabel, Long, Neo4jRelationship, RelationshipMetadata, RelationshipType, PropertyMetadata> xoSession) {
        this.graphDatabaseService = graphDatabaseService;
        this.xoSession = xoSession;
    }

    protected <T> ResultIterable<T> find(Class<T> type, Object value) {
        this.xoSession.flush();
        // get the label for the type
        EntityTypeMetadata<NodeMetadata> entityMetadata = xoSession.getEntityMetadata(type);
        Neo4jLabel label = entityMetadata.getDatastoreMetadata().getDiscriminator();
        // get the name of the indexed property
        PrimitivePropertyMethodMetadata<PropertyMetadata> propertyMethodMetadata = entityMetadata.getIndexedProperty().getPropertyMethodMetadata();
        PropertyMetadata datastoreMetadata = propertyMethodMetadata.getDatastoreMetadata();
        String propertyName = datastoreMetadata.getName();
        // convert the value from object to datastore representation
        Object datastoreValue = xoSession.toDatastore(value);
        // find the nodes
        ResourceIterator<Node> iterator = graphDatabaseService.findNodes(label.getLabel(), propertyName, datastoreValue);
        return xoSession.toResult(new ResultIterator<Neo4jNode>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Neo4jNode next() {
                return new Neo4jNode(iterator.next());
            }

            @Override
            public void close() {
                iterator.close();
            }
        });
    }

}
