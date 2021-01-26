package com.buschmais.xo.neo4j.spi;

import com.buschmais.xo.api.ResultIterable;
import com.buschmais.xo.neo4j.api.Neo4jRepository;
import com.buschmais.xo.neo4j.api.model.Neo4jLabel;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.api.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.api.metadata.type.EntityTypeMetadata;
import com.buschmais.xo.spi.session.XOSession;

/**
 * Abstract base implementation for Neo4j repositories.
 */
public abstract class AbstractNeo4jRepository<Label extends Neo4jLabel> implements Neo4jRepository {

    protected final XOSession<NodeMetadata<Label>, Label, ?, ?> xoSession;

    protected AbstractNeo4jRepository(XOSession<NodeMetadata<Label>, Label, ?, ?> xoSession) {
        this.xoSession = xoSession;
    }

    public <T> ResultIterable<T> find(Class<T> type, Object value) {
        this.xoSession.flush();
        // get the label for the type
        EntityTypeMetadata<NodeMetadata<Label>> entityMetadata = xoSession.getEntityMetadata(type);
        Label label = entityMetadata.getDatastoreMetadata().getDiscriminator();
        // get the name of the indexed property
        PrimitivePropertyMethodMetadata<PropertyMetadata> propertyMethodMetadata = entityMetadata.getIndexedProperty().getPropertyMethodMetadata();
        PropertyMetadata datastoreMetadata = propertyMethodMetadata.getDatastoreMetadata();
        Object datastoreValue = xoSession.toDatastore(value);
        return find(label, datastoreMetadata, datastoreValue);
    }

    protected abstract <T> ResultIterable<T> find(Label label, PropertyMetadata datastoreMetadata, Object datastoreValue);

}
