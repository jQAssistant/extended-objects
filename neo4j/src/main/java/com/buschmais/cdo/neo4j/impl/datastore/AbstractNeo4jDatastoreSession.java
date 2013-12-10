package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.ResultIterator;
import com.buschmais.cdo.spi.datastore.DatastorePropertyManager;
import com.buschmais.cdo.spi.metadata.MetadataProvider;
import com.buschmais.cdo.neo4j.api.annotation.Cypher;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.*;
import com.buschmais.cdo.spi.metadata.*;
import com.buschmais.cdo.spi.datastore.DatastoreSession;
import com.buschmais.cdo.spi.datastore.TypeSet;
import org.neo4j.graphdb.*;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractNeo4jDatastoreSession<GDS extends GraphDatabaseService> implements DatastoreSession<Long, Node, Long, Relationship> {

    private GDS graphDatabaseService;
    private MetadataProvider metadataProvider;
    private Neo4jPropertyManager propertyManager;

    public AbstractNeo4jDatastoreSession(GDS graphDatabaseService, MetadataProvider metadataProvider) {
        this.graphDatabaseService = graphDatabaseService;
        this.metadataProvider = metadataProvider;
        this.propertyManager = new Neo4jPropertyManager();
    }

    @Override
    public DatastorePropertyManager getDatastorePropertyManager() {
        return propertyManager;
    }

    public GDS getGraphDatabaseService() {
        return graphDatabaseService;
    }

    @Override
    public Node create(TypeSet types) {
        Node node = getGraphDatabaseService().createNode();
        Set<Label> labels = new HashSet<>();
        for (Class<?> currentType : types) {
            TypeMetadata<NodeMetadata> entityMetadata = metadataProvider.getEntityMetadata(currentType);
            labels.addAll(entityMetadata.getDatastoreMetadata().getAggregatedLabels());
        }
        for (Label label : labels) {
            node.addLabel(label);
        }
        return node;
    }

    @Override
    public ResultIterator<Node> find(Class<?> type, Object value) {
        TypeMetadata<NodeMetadata> typeMetadata = metadataProvider.getEntityMetadata(type);
        Label label = typeMetadata.getDatastoreMetadata().getLabel();
        if (label == null) {
            throw new CdoException("Type " + type.getName() + " has no label.");
        }
        IndexedPropertyMethodMetadata<?> indexedProperty = typeMetadata.getDatastoreMetadata().getIndexedProperty();
        if (indexedProperty == null) {
            indexedProperty = typeMetadata.getIndexedProperty();
        }
        if (indexedProperty == null) {
            throw new CdoException("Type " + typeMetadata.getType().getName() + " has no indexed property.");
        }
        PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> propertyMethodMetadata = indexedProperty.getPropertyMethodMetadata();
        ResourceIterable<Node> nodesByLabelAndProperty = getGraphDatabaseService().findNodesByLabelAndProperty(label, propertyMethodMetadata.getDatastoreMetadata().getName(), value);
        ResourceIterator<Node> iterator = nodesByLabelAndProperty.iterator();
        return new ResourceResultIterator(iterator);
    }

    @Override
    public void migrate(Node entity, TypeSet types, TypeSet targetTypes) {
        Set<Label> labels = getLabels(types);
        Set<Label> targetLabels = getLabels(targetTypes);
        Set<Label> labelsToRemove = new HashSet<>(labels);
        labelsToRemove.removeAll(targetLabels);
        for (Label label : labelsToRemove) {
            entity.removeLabel(label);
        }
        Set<Label> labelsToAdd = new HashSet<>(targetLabels);
        labelsToAdd.removeAll(labels);
        for (Label label : labelsToAdd) {
            entity.addLabel(label);
        }
    }

    @Override
    public boolean isEntity(Object o) {
        return Node.class.isAssignableFrom(o.getClass());
    }

    @Override
    public Long getId(Node entity) {
        return Long.valueOf(entity.getId());
    }

    @Override
    public void delete(Node node) {
        node.delete();
    }

    protected <QL> String getCypher(QL expression) {
        if (expression instanceof String) {
            return (String) expression;
        } else if (expression instanceof Class<?>) {
            Class<?> typeExpression = (Class) expression;
            Cypher cypher = typeExpression.getAnnotation(Cypher.class);
            if (cypher == null) {
                throw new CdoException(typeExpression.getName() + " must be annotated with " + Cypher.class.getName());
            }
            return cypher.value();
        }
        throw new CdoException("Unsupported query expression " + expression);
    }

    private Set<Label> getLabels(TypeSet types) {
        Set<Label> labels = new HashSet<>();
        for (Class<?> type : types) {
            TypeMetadata<NodeMetadata> typeMetadata = metadataProvider.getEntityMetadata(type);
            NodeMetadata datastoreMetadata = typeMetadata.getDatastoreMetadata();
            if (datastoreMetadata != null) {
                labels.addAll(datastoreMetadata.getAggregatedLabels());
            }
        }
        return labels;
    }
}
