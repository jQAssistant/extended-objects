package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.EnumPropertyMetadata;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.PrimitivePropertyMetadata;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.cdo.spi.datastore.DatastorePropertyManager;
import com.buschmais.cdo.spi.metadata.EnumPropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.PrimitivePropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.RelationMetadata;
import org.neo4j.graphdb.*;

public class Neo4jPropertyManager implements DatastorePropertyManager<Node, Relationship, PrimitivePropertyMetadata, EnumPropertyMetadata, RelationshipMetadata> {

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

}
