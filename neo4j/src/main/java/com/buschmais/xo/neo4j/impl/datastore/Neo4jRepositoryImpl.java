package com.buschmais.xo.neo4j.impl.datastore;

import com.buschmais.xo.api.ResultIterable;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.neo4j.api.Neo4jRepository;
import com.buschmais.xo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.RelationshipType;
import com.buschmais.xo.spi.session.XOSession;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;
import org.neo4j.graphdb.*;

/**
 * Implementation of the {@link Neo4jRepository}.
 */
public class Neo4jRepositoryImpl implements Neo4jRepository {

    private final GraphDatabaseService graphDatabaseService;
    private final XOSession<Long, Node, NodeMetadata, Label, Long, Relationship, RelationshipMetadata, RelationshipType, PropertyMetadata> xoSession;

    public Neo4jRepositoryImpl(
            GraphDatabaseService graphDatabaseService,
            XOSession<Long, Node, NodeMetadata, Label, Long, Relationship, RelationshipMetadata, RelationshipType, PropertyMetadata> xoSession) {
        this.graphDatabaseService = graphDatabaseService;
        this.xoSession = xoSession;
    }

    @Override
    public <T> ResultIterable<T> find(Class<T> type, Object value) {
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
        ResourceIterable<Node> nodesIterable = graphDatabaseService.findNodesByLabelAndProperty(label, propertyName, datastoreValue);
        ResourceIterator<Node> iterator = nodesIterable.iterator();
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
