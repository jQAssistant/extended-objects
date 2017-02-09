package com.buschmais.xo.neo4j.embedded.impl.datastore;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;

import com.buschmais.xo.api.ResultIterable;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedLabel;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedNode;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationship;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationshipType;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;
import com.buschmais.xo.spi.session.XOSession;

/**
 * Abstract base implementation for Neo4j repositories.
 */
abstract class AbstractNeo4jRepositoryImpl {

    private final GraphDatabaseService graphDatabaseService;
    private final XOSession<Long, EmbeddedNode, NodeMetadata<EmbeddedLabel>, EmbeddedLabel, Long, EmbeddedRelationship, RelationshipMetadata<EmbeddedRelationshipType>, EmbeddedRelationshipType, PropertyMetadata> xoSession;

    protected AbstractNeo4jRepositoryImpl(GraphDatabaseService graphDatabaseService,
            XOSession<Long, EmbeddedNode, NodeMetadata<EmbeddedLabel>, EmbeddedLabel, Long, EmbeddedRelationship, RelationshipMetadata<EmbeddedRelationshipType>, EmbeddedRelationshipType, PropertyMetadata> xoSession) {
        this.graphDatabaseService = graphDatabaseService;
        this.xoSession = xoSession;
    }

    protected <T> ResultIterable<T> find(Class<T> type, Object value) {
        this.xoSession.flush();
        // get the label for the type
        EntityTypeMetadata<NodeMetadata<EmbeddedLabel>> entityMetadata = xoSession.getEntityMetadata(type);
        EmbeddedLabel label = entityMetadata.getDatastoreMetadata().getDiscriminator();
        // get the name of the indexed property
        PrimitivePropertyMethodMetadata<PropertyMetadata> propertyMethodMetadata = entityMetadata.getIndexedProperty().getPropertyMethodMetadata();
        PropertyMetadata datastoreMetadata = propertyMethodMetadata.getDatastoreMetadata();
        String propertyName = datastoreMetadata.getName();
        // convert the value from object to datastore representation
        Object datastoreValue = xoSession.toDatastore(value);
        // find the nodes
        ResourceIterator<Node> iterator = graphDatabaseService.findNodes(label.getDelegate(), propertyName, datastoreValue);
        return xoSession.toResult(new ResultIterator<EmbeddedNode>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public EmbeddedNode next() {
                return new EmbeddedNode(iterator.next());
            }

            @Override
            public void close() {
                iterator.close();
            }
        });
    }

}
