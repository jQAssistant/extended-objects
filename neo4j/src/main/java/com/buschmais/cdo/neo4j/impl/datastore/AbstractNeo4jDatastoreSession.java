package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.ResultIterator;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.*;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.cdo.neo4j.impl.node.metadata.*;
import com.buschmais.cdo.neo4j.spi.DatastoreSession;
import com.buschmais.cdo.neo4j.spi.TypeSet;
import org.neo4j.graphdb.*;

import java.util.HashSet;
import java.util.Iterator;
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
            labels.addAll(metadataProvider.getEntityMetadata(currentType).getAggregatedLabels());
        }
        for (Label label : labels) {
            node.addLabel(label);
        }
        return node;
    }

    @Override
    public ResultIterator<Node> find(Class<?> type, Object value) {
        EntityMetadata entityMetadata = metadataProvider.getEntityMetadata(type);
        Label label = entityMetadata.getLabel();
        if (label == null) {
            throw new CdoException("Type " + type.getName() + " has no label.");
        }
        IndexedPropertyMethodMetadata indexedProperty = entityMetadata.getIndexedProperty();
        if (indexedProperty == null) {
            throw new CdoException("Type " + entityMetadata.getType().getName() + " has no indexed property.");
        }
        PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> propertyMethodMetadata = indexedProperty.getPropertyMethodMetadata();
        ResourceIterable<Node> nodesByLabelAndProperty = getGraphDatabaseService().findNodesByLabelAndProperty(label, propertyMethodMetadata.getDatastoreMetadata().getName(), value);
        ResourceIterator<Node> iterator = nodesByLabelAndProperty.iterator();
        return new ResourceResultIterator(iterator);
    }

    @Override
    public void migrate(Node entity, TypeSet types, TypeSet targetTypes) {
        Set<Label> labels = new HashSet<>();
        for (Class<?> type : types) {
            EntityMetadata entityMetadata = metadataProvider.getEntityMetadata(type);
            labels.addAll(entityMetadata.getAggregatedLabels());
        }
        Set<Label> targetLabels = new HashSet<>();
        for (Class<?> currentType : targetTypes) {
            EntityMetadata targetMetadata = metadataProvider.getEntityMetadata(currentType);
            targetLabels.addAll(targetMetadata.getAggregatedLabels());
        }
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
    public TypeSet getTypes(Node entity) {
        // Collect all labels from the node
        Set<Label> labels = new HashSet<>();
        for (Label label : entity.getLabels()) {
            labels.add(label);
        }
        // Get all types matching the labels
        Set<Class<?>> types = new HashSet<>();
        for (Label label : labels) {
            Set<EntityMetadata> entityMetadataOfLabel = metadataProvider.getEntityMetadata(label);
            if (entityMetadataOfLabel != null) {
                for (EntityMetadata entityMetadata : entityMetadataOfLabel) {
                    if (labels.containsAll(entityMetadata.getAggregatedLabels())) {
                        types.add(entityMetadata.getType());
                    }
                }
            }
        }
        TypeSet uniqueTypes = new TypeSet();
        // Remove super types if subtypes are already in the type set
        for (Class<?> type : types) {
            boolean subtype = false;
            for (Iterator<Class<?>> subTypeIterator = types.iterator(); subTypeIterator.hasNext() && !subtype; ) {
                Class<?> otherType = subTypeIterator.next();
                if (!type.equals(otherType) && type.isAssignableFrom(otherType)) {
                    subtype = true;
                }
            }
            if (!subtype) {
                uniqueTypes.add(type);
            }
        }
        return uniqueTypes;
    }

    @Override
    public Long getId(Node entity) {
        return Long.valueOf(entity.getId());
    }

    @Override
    public void delete(Node node) {
        node.delete();
    }

    // Relations

    @Override
    public boolean hasRelation(Node source, com.buschmais.cdo.neo4j.impl.node.metadata.RelationMetadata<RelationshipMetadata> metadata, com.buschmais.cdo.neo4j.impl.node.metadata.RelationMetadata.Direction direction) {
        return source.hasRelationship(metadata.getDatastoreMetadata().getRelationshipType(), getDirection(direction));
    }

    @Override
    public Relationship getSingleRelation(Node source, com.buschmais.cdo.neo4j.impl.node.metadata.RelationMetadata<RelationshipMetadata> metadata, com.buschmais.cdo.neo4j.impl.node.metadata.RelationMetadata.Direction direction) {
        return source.getSingleRelationship(metadata.getDatastoreMetadata().getRelationshipType(), getDirection(direction));
    }

    @Override
    public Iterable<Relationship> getRelations(Node source, com.buschmais.cdo.neo4j.impl.node.metadata.RelationMetadata<RelationshipMetadata> metadata, com.buschmais.cdo.neo4j.impl.node.metadata.RelationMetadata.Direction direction) {
        return source.getRelationships(metadata.getDatastoreMetadata().getRelationshipType(), getDirection(direction));
    }

    @Override
    public Relationship createRelation(Node source, com.buschmais.cdo.neo4j.impl.node.metadata.RelationMetadata<RelationshipMetadata> metadata, com.buschmais.cdo.neo4j.impl.node.metadata.RelationMetadata.Direction direction, Node target) {
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


    private Direction getDirection(com.buschmais.cdo.neo4j.impl.node.metadata.RelationMetadata.Direction direction) {
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
}
