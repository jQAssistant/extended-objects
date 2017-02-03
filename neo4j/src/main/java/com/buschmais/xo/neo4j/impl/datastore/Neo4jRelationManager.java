package com.buschmais.xo.neo4j.impl.datastore;

import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.api.model.Neo4jDirection;
import com.buschmais.xo.neo4j.api.model.Neo4jNode;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationship;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationshipType;
import com.buschmais.xo.neo4j.impl.datastore.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.DatastoreRelationManager;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;

/**
 * Implementation of a
 * {@link com.buschmais.xo.spi.datastore.DatastoreRelationManager} for Neo4j.
 */
public class Neo4jRelationManager extends AbstractNeo4jPropertyManager<Neo4jRelationship>
        implements DatastoreRelationManager<Neo4jNode, Long, Neo4jRelationship, RelationshipMetadata, Neo4jRelationshipType, PropertyMetadata> {

    private final GraphDatabaseService graphDatabaseService;

    /**
     * Constructor.
     * 
     * @param graphDatabaseService
     *            The graph database service.
     */
    public Neo4jRelationManager(GraphDatabaseService graphDatabaseService) {
        this.graphDatabaseService = graphDatabaseService;
    }

    @Override
    public boolean isRelation(Object o) {
        return Neo4jRelationship.class.isAssignableFrom(o.getClass());
    }

    @Override
    public Neo4jRelationshipType getRelationDiscriminator(Neo4jRelationship relationship) {
        return relationship.getType();
    }

    @Override
    public Neo4jRelationship createRelation(Neo4jNode source, RelationTypeMetadata<RelationshipMetadata> metadata, RelationTypeMetadata.Direction direction,
            Neo4jNode target, Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> example) {
        Neo4jRelationship relationship;
        Neo4jRelationshipType relationshipType = metadata.getDatastoreMetadata().getDiscriminator();
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
    public void deleteRelation(Neo4jRelationship relationship) {
        relationship.delete();
    }

    @Override
    public Long getRelationId(Neo4jRelationship relationship) {
        return relationship.getId();
    }

    @Override
    public Neo4jRelationship findRelationById(RelationTypeMetadata<RelationshipMetadata> metadata, Long id) {
        return new Neo4jRelationship(graphDatabaseService.getRelationshipById(id));
    }

    @Override
    public void flushRelation(Neo4jRelationship relationship) {
        relationship.flush();
    }

    @Override
    public void clearRelation(Neo4jRelationship neo4jRelationship) {
        neo4jRelationship.clear();
    }

    @Override
    public boolean hasSingleRelation(Neo4jNode source, RelationTypeMetadata<RelationshipMetadata> metadata, RelationTypeMetadata.Direction direction) {
        return source.hasRelationship(metadata.getDatastoreMetadata().getDiscriminator(), getDirection(direction));
    }

    @Override
    public Neo4jRelationship getSingleRelation(Neo4jNode source, RelationTypeMetadata<RelationshipMetadata> metadata,
            RelationTypeMetadata.Direction direction) {
        return source.getSingleRelationship(metadata.getDatastoreMetadata().getDiscriminator(), getDirection(direction));
    }

    @Override
    public Iterable<Neo4jRelationship> getRelations(Neo4jNode source, RelationTypeMetadata<RelationshipMetadata> metadata,
            RelationTypeMetadata.Direction direction) {
        return source.getRelationships(metadata.getDatastoreMetadata().getDiscriminator(), getDirection(direction));
    }

    @Override
    public Neo4jNode getFrom(Neo4jRelationship relationship) {
        return relationship.getStartNode();
    }

    @Override
    public Neo4jNode getTo(Neo4jRelationship relationship) {
        return relationship.getEndNode();
    }

    private Neo4jDirection getDirection(RelationTypeMetadata.Direction direction) {
        switch (direction) {
        case FROM:
            return Neo4jDirection.OUTGOING;
        case TO:
            return Neo4jDirection.INCOMING;
        default:
            throw new XOException("Unsupported direction " + direction);
        }
    }

}
