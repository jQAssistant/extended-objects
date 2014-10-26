package com.buschmais.xo.neo4j.impl.datastore;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.impl.datastore.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.RelationshipType;
import com.buschmais.xo.spi.datastore.DatastoreRelationManager;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import java.util.Map;

/**
 * Implementation of a {@link com.buschmais.xo.spi.datastore.DatastoreRelationManager} for Neo4j.
 */
public class Neo4jRelationManager extends AbstractNeo4jPropertyManager<Relationship> implements DatastoreRelationManager<Node, Long, Relationship, RelationshipMetadata, RelationshipType, PropertyMetadata> {

    private final GraphDatabaseService graphDatabaseService;

    /**
     * Constructor.
     * @param graphDatabaseService The graph database service.
     */
    public Neo4jRelationManager(GraphDatabaseService graphDatabaseService) {
        this.graphDatabaseService = graphDatabaseService;
    }

    @Override
    public boolean isRelation(Object o) {
        return Relationship.class.isAssignableFrom(o.getClass());
    }

    @Override
    public RelationshipType getRelationDiscriminator(Relationship relationship) {
        return new RelationshipType(relationship.getType());
    }

    @Override
    public Relationship createRelation(Node source, RelationTypeMetadata<RelationshipMetadata> metadata, RelationTypeMetadata.Direction direction, Node target, Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> example) {
        Relationship relationship;
        switch (direction) {
            case FROM:
                relationship = source.createRelationshipTo(target, metadata.getDatastoreMetadata().getDiscriminator());
                break;
            case TO:
                relationship = target.createRelationshipTo(source, metadata.getDatastoreMetadata().getDiscriminator());
                break;
            default:
                throw new XOException("Unsupported direction " + direction);
        }
        setProperties(relationship, example);
        return relationship;
    }

    @Override
    public void deleteRelation(Relationship relationship) {
        relationship.delete();
    }

    @Override
    public Long getRelationId(Relationship relationship) {
        return relationship.getId();
    }

    @Override
    public Relationship findRelationById(RelationTypeMetadata<RelationshipMetadata> metadata, Long id) {
        return graphDatabaseService.getRelationshipById(id);
    }

    @Override
    public void flushRelation(Relationship relationship) {
    }

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
    public Node getFrom(Relationship relationship) {
        return relationship.getStartNode();
    }

    @Override
    public Node getTo(Relationship relationship) {
        return relationship.getEndNode();
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

}
