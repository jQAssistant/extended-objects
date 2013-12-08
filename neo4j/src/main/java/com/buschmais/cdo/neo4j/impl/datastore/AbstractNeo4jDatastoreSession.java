package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.ResultIterator;
import com.buschmais.cdo.neo4j.impl.node.metadata.*;
import com.buschmais.cdo.neo4j.impl.node.metadata.neo4j.EnumPropertyMetadata;
import com.buschmais.cdo.neo4j.impl.node.metadata.neo4j.PrimitivePropertyMetadata;
import com.buschmais.cdo.neo4j.impl.node.metadata.neo4j.RelationPropertyMetadata;
import com.buschmais.cdo.neo4j.spi.DatastoreSession;
import com.buschmais.cdo.neo4j.spi.TypeSet;
import org.neo4j.graphdb.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractNeo4jDatastoreSession<GDS extends GraphDatabaseService> implements DatastoreSession<Long, Node, Long, Relationship, PrimitivePropertyMetadata, EnumPropertyMetadata, RelationPropertyMetadata> {

    private GDS graphDatabaseService;
    private NodeMetadataProvider metadataProvider;

    public AbstractNeo4jDatastoreSession(GDS graphDatabaseService, NodeMetadataProvider metadataProvider) {
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
            labels.addAll(metadataProvider.getNodeMetadata(currentType).getAggregatedLabels());
        }
        for (Label label : labels) {
            node.addLabel(label);
        }
        return node;
    }

    @Override
    public ResultIterator<Node> find(Class<?> type, Object value) {
        NodeMetadata nodeMetadata = metadataProvider.getNodeMetadata(type);
        Label label = nodeMetadata.getLabel();
        if (label == null) {
            throw new CdoException("Type " + type.getName() + " has no label.");
        }
        IndexedPropertyMethodMetadata indexedProperty = nodeMetadata.getIndexedProperty();
        if (indexedProperty == null) {
            throw new CdoException("Type " + nodeMetadata.getType().getName() + " has no indexed property.");
        }
        ResourceIterable<Node> nodesByLabelAndProperty = getGraphDatabaseService().findNodesByLabelAndProperty(label, indexedProperty.getPropertyMethodMetadata().getPropertyName(), value);
        ResourceIterator<Node> iterator = nodesByLabelAndProperty.iterator();
        return new ResourceResultIterator(iterator);
    }

    @Override
    public void migrate(Node entity, TypeSet types, TypeSet targetTypes) {
        Set<Label> labels = new HashSet<>();
        for (Class<?> type : types) {
            NodeMetadata nodeMetadata = metadataProvider.getNodeMetadata(type);
            labels.addAll(nodeMetadata.getAggregatedLabels());
        }
        Set<Label> targetLabels = new HashSet<>();
        for (Class<?> currentType : targetTypes) {
            NodeMetadata targetMetadata = metadataProvider.getNodeMetadata(currentType);
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
            Set<NodeMetadata> nodeMetadataOfLabel = metadataProvider.getNodeMetadata(label);
            if (nodeMetadataOfLabel != null) {
                for (NodeMetadata nodeMetadata : nodeMetadataOfLabel) {
                    if (labels.containsAll(nodeMetadata.getAggregatedLabels())) {
                        types.add(nodeMetadata.getType());
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
    public boolean hasRelation(Node source, RelationshipMetadata<RelationPropertyMetadata> metadata, RelationshipMetadata.Direction direction) {
        return source.hasRelationship(metadata.getRelationshipType(), getDirection(direction));
    }

    @Override
    public Relationship getSingleRelation(Node source, RelationshipMetadata<RelationPropertyMetadata> metadata, RelationshipMetadata.Direction direction) {
        return source.getSingleRelationship(metadata.getRelationshipType(), getDirection(direction));
    }

    @Override
    public Iterable<Relationship> getRelations(Node source, RelationshipMetadata<RelationPropertyMetadata> metadata, RelationshipMetadata.Direction direction) {
        return source.getRelationships(metadata.getRelationshipType(), getDirection(direction));
    }

    @Override
    public Relationship createRelation(Node source, RelationshipMetadata<RelationPropertyMetadata> metadata, RelationshipMetadata.Direction direction, Node target) {
        switch (direction) {
            case OUTGOING:
                return source.createRelationshipTo(target, metadata.getRelationshipType());
            case INCOMING:
                return target.createRelationshipTo(source, metadata.getRelationshipType());
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


    private Direction getDirection(RelationshipMetadata.Direction direction) {
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
        node.removeProperty(metadata.getPropertyName());
    }

    @Override
    public boolean hasProperty(Node node, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata) {
        return node.hasProperty(metadata.getPropertyName());
    }

    @Override
    public void setProperty(Node node, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata, Object value) {
        node.setProperty(metadata.getPropertyName(), value);
    }

    @Override
    public Object getProperty(Node node, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata) {
        return node.getProperty(metadata.getPropertyName());
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
