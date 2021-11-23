package com.buschmais.xo.neo4j.embedded.impl.datastore;

import java.util.Map;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.api.metadata.type.RelationTypeMetadata;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedDirection;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedNode;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationship;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationshipType;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.DatastoreRelationManager;

/**
 * Implementation of a
 * {@link com.buschmais.xo.spi.datastore.DatastoreRelationManager} for Neo4j.
 */
public class EmbeddedRelationManager extends AbstractEmbeddedPropertyManager<EmbeddedRelationship> implements
        DatastoreRelationManager<EmbeddedNode, Long, EmbeddedRelationship, RelationshipMetadata<EmbeddedRelationshipType>, EmbeddedRelationshipType, PropertyMetadata> {

    private final EmbeddedNeo4jDatastoreTransaction datastoreTransaction;

    public EmbeddedRelationManager(EmbeddedDatastoreTransaction datastoreTransaction) {
        this.datastoreTransaction = datastoreTransaction;
    }

    @Override
    public boolean isRelation(Object o) {
        return EmbeddedRelationship.class.isAssignableFrom(o.getClass());
    }

    @Override
    public EmbeddedRelationshipType getRelationDiscriminator(EmbeddedRelationship relationship) {
        return relationship.getType();
    }

    @Override
    public EmbeddedRelationship createRelation(EmbeddedNode source, RelationTypeMetadata<RelationshipMetadata<EmbeddedRelationshipType>> metadata,
            RelationTypeMetadata.Direction direction, EmbeddedNode target, Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> example) {
        EmbeddedRelationship relationship;
        EmbeddedRelationshipType relationshipType = metadata.getDatastoreMetadata().getDiscriminator();
        switch (direction) {
        case FROM:
            relationship = source.createRelationshipTo(target, relationshipType);
            break;
        case TO:
            relationship = target.createRelationshipTo(source, relationshipType);
            break;
        default:
            throw new XOException("Unsupported direction " + direction);
        }
        setProperties(relationship, example);
        return relationship;
    }

    @Override
    public void deleteRelation(EmbeddedRelationship relationship) {
        relationship.delete();
    }

    @Override
    public Long getRelationId(EmbeddedRelationship relationship) {
        return relationship.getId();
    }

    @Override
    public EmbeddedRelationship findRelationById(RelationTypeMetadata<RelationshipMetadata<EmbeddedRelationshipType>> metadata, Long id) {
        return new EmbeddedRelationship(datastoreTransaction, datastoreTransaction.getTransaction().getRelationshipById(id));
    }

    @Override
    public EmbeddedRelationship getSingleRelation(EmbeddedNode source, RelationTypeMetadata<RelationshipMetadata<EmbeddedRelationshipType>> metadata,
            RelationTypeMetadata.Direction direction) {
        return source.getSingleRelationship(metadata.getDatastoreMetadata().getDiscriminator(), getDirection(direction));
    }

    @Override
    public Iterable<EmbeddedRelationship> getRelations(EmbeddedNode source, RelationTypeMetadata<RelationshipMetadata<EmbeddedRelationshipType>> metadata,
            RelationTypeMetadata.Direction direction) {
        return source.getRelationships(metadata.getDatastoreMetadata().getDiscriminator(), getDirection(direction));
    }

    @Override
    public EmbeddedNode getFrom(EmbeddedRelationship relationship) {
        return relationship.getStartNode();
    }

    @Override
    public EmbeddedNode getTo(EmbeddedRelationship relationship) {
        return relationship.getEndNode();
    }

    private EmbeddedDirection getDirection(RelationTypeMetadata.Direction direction) {
        switch (direction) {
        case FROM:
            return EmbeddedDirection.OUTGOING;
        case TO:
            return EmbeddedDirection.INCOMING;
        default:
            throw new XOException("Unsupported direction " + direction);
        }
    }

    @Override
    public void flush(Iterable<EmbeddedRelationship> relationships) {
        for (EmbeddedRelationship relationship : relationships) {
            relationship.flush();
        }
    }

    @Override
    public void afterCompletion(EmbeddedRelationship relationship, boolean clear) {
        if (clear) {
            relationship.clear();
        }
    }
}
