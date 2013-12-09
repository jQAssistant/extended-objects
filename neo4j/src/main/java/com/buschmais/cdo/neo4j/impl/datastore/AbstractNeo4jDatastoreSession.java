package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.ResultIterator;
import com.buschmais.cdo.spi.metadata.MetadataProvider;
import com.buschmais.cdo.neo4j.api.annotation.Cypher;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.*;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.cdo.spi.metadata.*;
import com.buschmais.cdo.spi.datastore.DatastoreSession;
import com.buschmais.cdo.spi.datastore.TypeSet;
import org.neo4j.graphdb.*;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractNeo4jDatastoreSession<GDS extends GraphDatabaseService> implements DatastoreSession<Long, Node, Long, Relationship, PrimitivePropertyMetadata, EnumPropertyMetadata, RelationshipMetadata> {

    private GDS graphDatabaseService;
    private MetadataProvider metadataProvider;

    public AbstractNeo4jDatastoreSession(GDS graphDatabaseService, MetadataProvider metadataProvider) {
        this.graphDatabaseService = graphDatabaseService;
        this.metadataProvider = metadataProvider;
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
        IndexedPropertyMethodMetadata indexedProperty = typeMetadata.getIndexedProperty();
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

    // Relations

    @Override
    public boolean hasRelation(Node source, RelationMetadata<RelationshipMetadata> metadata, RelationMetadata.Direction direction) {
        return source.hasRelationship(metadata.getDatastoreMetadata().getRelationshipType(), getDirection(direction));
    }

    @Override
    public Relationship getSingleRelation(Node source, RelationMetadata<RelationshipMetadata> metadata, RelationMetadata.Direction direction) {
        return source.getSingleRelationship(metadata.getDatastoreMetadata().getRelationshipType(), getDirection(direction));
    }

    @Override
    public Iterable<Relationship> getRelations(Node source, RelationMetadata<RelationshipMetadata> metadata, RelationMetadata.Direction direction) {
        return source.getRelationships(metadata.getDatastoreMetadata().getRelationshipType(), getDirection(direction));
    }

    @Override
    public Relationship createRelation(Node source, RelationMetadata<RelationshipMetadata> metadata, RelationMetadata.Direction direction, Node target) {
        switch (direction) {
            case OUTGOING:
                return source.createRelationshipTo(target, metadata.getDatastoreMetadata().getRelationshipType());
            case INCOMING:
                return target.createRelationshipTo(source, metadata.getDatastoreMetadata().getRelationshipType());
            default:
                throw new CdoException("Unsupported direction " + direction);
        }
    }

    @Override
    public void deleteRelation(Relationship relationship) {
        relationship.delete();
    }


    @Override
    public Node getTarget(Relationship relationship) {
        return relationship.getEndNode();
    }

    @Override
    public Node getSource(Relationship relationship) {
        return relationship.getStartNode();
    }


    private Direction getDirection(RelationMetadata.Direction direction) {
        switch (direction) {
            case OUTGOING:
                return Direction.OUTGOING;
            case INCOMING:
                return Direction.INCOMING;
            default:
                throw new CdoException("Unsupported direction " + direction);
        }
    }

    // Properties

    @Override
    public void removeProperty(Node node, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata) {
        node.removeProperty(metadata.getDatastoreMetadata().getName());
    }

    @Override
    public boolean hasProperty(Node node, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata) {
        return node.hasProperty(metadata.getDatastoreMetadata().getName());
    }

    @Override
    public void setProperty(Node node, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata, Object value) {
        node.setProperty(metadata.getDatastoreMetadata().getName(), value);
    }

    @Override
    public Object getProperty(Node node, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata) {
        return node.getProperty(metadata.getDatastoreMetadata().getName());
    }

    @Override
    public Enum<?> getEnumProperty(Node node, EnumPropertyMethodMetadata<EnumPropertyMetadata> metadata) {
        for (Enum<?> enumerationValue : metadata.getEnumerationType().getEnumConstants()) {
            if (node.hasLabel(DynamicLabel.label(enumerationValue.name()))) {
                return enumerationValue;
            }
        }
        return null;
    }

    @Override
    public void setEnumProperty(Node node, EnumPropertyMethodMetadata<EnumPropertyMetadata> metadata, Object value) {
        for (Enum<?> enumerationValue : metadata.getEnumerationType().getEnumConstants()) {
            Label label = DynamicLabel.label(enumerationValue.name());
            if (enumerationValue.equals(value)) {
                node.addLabel(label);
            } else if (node.hasLabel(label)) {
                node.removeLabel(label);
            }
        }
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
