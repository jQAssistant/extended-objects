package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.EnumPropertyMetadata;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.PrimitivePropertyMetadata;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.cdo.spi.datastore.DatastorePropertyManager;
import com.buschmais.cdo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class Neo4jPropertyManager implements DatastorePropertyManager<Node, Relationship, PrimitivePropertyMetadata, EnumPropertyMetadata, RelationshipMetadata> {

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
            case OUTGOING:
                return source.createRelationshipTo(target, metadata.getDatastoreMetadata().getDiscriminator());
            case INCOMING:
                return target.createRelationshipTo(source, metadata.getDatastoreMetadata().getDiscriminator());
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


    private Direction getDirection(RelationTypeMetadata.Direction direction) {
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
    public void removeRelationProperty(Relationship relationship, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata) {
        relationship.removeProperty(metadata.getDatastoreMetadata().getName());
    }

    @Override
    public boolean hasProperty(Node node, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata) {
        return node.hasProperty(metadata.getDatastoreMetadata().getName());
    }

    @Override
    public boolean hasRelationProperty(Relationship relationship, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata) {
        return relationship.hasProperty(metadata.getDatastoreMetadata().getName());
    }

    @Override
    public void setEntityProperty(Node node, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata, Object value) {
        node.setProperty(metadata.getDatastoreMetadata().getName(), value);
    }

    @Override
    public void setRelationProperty(Relationship relationship, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata, Object value) {
        relationship.setProperty(metadata.getDatastoreMetadata().getName(), value);
    }

    @Override
    public Object getProperty(Node node, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata) {
        return node.getProperty(metadata.getDatastoreMetadata().getName());
    }

    @Override
    public Object getRelationProperty(Relationship relationship, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata) {
        return relationship.getProperty(metadata.getDatastoreMetadata().getName());
    }
}
