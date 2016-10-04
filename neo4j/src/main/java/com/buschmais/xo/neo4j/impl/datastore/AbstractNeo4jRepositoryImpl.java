package com.buschmais.xo.neo4j.impl.datastore;

import org.neo4j.graphdb.*;

import com.buschmais.xo.api.ResultIterable;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.neo4j.api.Neo4jRepository;
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
    private final XOSession<Long, Node, NodeMetadata, Label, Long, Relationship, RelationshipMetadata, RelationshipType, PropertyMetadata> xoSession;

    protected AbstractNeo4jRepositoryImpl(GraphDatabaseService graphDatabaseService,
            XOSession<Long, Node, NodeMetadata, Label, Long, Relationship, RelationshipMetadata, RelationshipType, PropertyMetadata> xoSession) {
        this.graphDatabaseService = graphDatabaseService;
        this.xoSession = xoSession;
    }

    protected <T> ResultIterable<T> find(Class<T> type, Object value) {
        // get the label for the type
        EntityTypeMetadata<NodeMetadata> entityMetadata = xoSession.getEntityMetadata(type);
        Label label = entityMetadata.getDatastoreMetadata().getDiscriminator();
        // get the name of the indexed property
        PrimitivePropertyMethodMetadata<PropertyMetadata> propertyMethodMetadata = entityMetadata.getIndexedProperty().getPropertyMethodMetadata();
        PropertyMetadata datastoreMetadata = propertyMethodMetadata.getDatastoreMetadata();
        String propertyName = datastoreMetadata.getName();
        // convert the value from object to datastore representation
        Object datastoreValue = xoSession.toDatastore(value);
        // find the nodes
        ResourceIterator<Node> iterator = graphDatabaseService.findNodes(label, propertyName, datastoreValue);
        return xoSession.toResult(new ResultIterator<Node>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Node next() {
                return iterator.next();
            }

            @Override
            public void close() {
                iterator.close();
            }
        });
    }

}
