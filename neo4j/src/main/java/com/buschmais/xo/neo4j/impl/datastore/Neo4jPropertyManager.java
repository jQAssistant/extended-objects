package com.buschmais.xo.neo4j.impl.datastore;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.impl.datastore.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.DatastorePropertyManager;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class Neo4jPropertyManager implements DatastorePropertyManager<Node, Relationship, PropertyMetadata, RelationshipMetadata> {

    @Override
    public boolean hasSingleRelation(Node source, RelationTypeMetadata<RelationshipMetadata> metadata, RelationTypeMetadata.Direction direction) {
        return source.hasRelationship(metadata.getDatastoreMetadata().getDiscriminator(), getDirection(direction));
    }

    @Override
    public Relationship getSingleRelation(Node source, RelationTypeMetadata<RelationshipMetadata> metadata, RelationTypeMetadata.Direction direction) {
        return source.getSingleRelationship(metadata.getDatastoreMetadata().getDiscriminator(), getDirection(direction));
    }

    @Override
    public Iterable<Relationship> getRelations(Node source, RelationTypeMetadata<RelationshipMetadata> metadata, RelationTypeMetadata.Direction direction) {
        return source.getRelationships(metadata.getDatastoreMetadata().getDiscriminator(), getDirection(direction));
    }

    @Override
    public Relationship createRelation(Node source, RelationTypeMetadata<RelationshipMetadata> metadata, RelationTypeMetadata.Direction direction, Node target) {
        switch (direction) {
            case FROM:
                return source.createRelationshipTo(target, metadata.getDatastoreMetadata().getDiscriminator());
            case TO:
                return target.createRelationshipTo(source, metadata.getDatastoreMetadata().getDiscriminator());
            default:
                throw new XOException("Unsupported direction " + direction);
        }
    }

    @Override
    public void deleteRelation(Relationship relationship) {
        relationship.delete();
    }


    @Override
    public Node getTo(Relationship relationship) {
        return relationship.getEndNode();
    }

    @Override
    public Node getFrom(Relationship relationship) {
        return relationship.getStartNode();
    }


    private Direction getDirection(RelationTypeMetadata.Direction direction) {
        switch (direction) {
            case FROM:
                return Direction.OUTGOING;
            case TO:
                return Direction.INCOMING;
            default:
                throw new XOException("Unsupported direction " + direction);
        }
    }

    // Properties

    @Override
    public void removeEntityProperty(Node node, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        node.removeProperty(metadata.getDatastoreMetadata().getName());
    }

    @Override
    public void removeRelationProperty(Relationship relationship, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        relationship.removeProperty(metadata.getDatastoreMetadata().getName());
    }

    @Override
    public boolean hasEntityProperty(Node node, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        return node.hasProperty(metadata.getDatastoreMetadata().getName());
    }

    @Override
    public boolean hasRelationProperty(Relationship relationship, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        return relationship.hasProperty(metadata.getDatastoreMetadata().getName());
    }

    @Override
    public void setEntityProperty(Node node, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata, Object value) {
        node.setProperty(metadata.getDatastoreMetadata().getName(), value);
    }

    @Override
    public void setRelationProperty(Relationship relationship, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata, Object value) {
        relationship.setProperty(metadata.getDatastoreMetadata().getName(), value);
    }

    @Override
    public Object getEntityProperty(Node node, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        return node.getProperty(metadata.getDatastoreMetadata().getName());
    }

    @Override
    public Object getRelationProperty(Relationship relationship, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        return relationship.getProperty(metadata.getDatastoreMetadata().getName());
    }
}
