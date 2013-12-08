package com.buschmais.cdo.neo4j.spi;

import com.buschmais.cdo.api.ResultIterator;
import com.buschmais.cdo.neo4j.impl.node.metadata.EnumPropertyMethodMetadata;
import com.buschmais.cdo.neo4j.impl.node.metadata.PrimitivePropertyMethodMetadata;
import com.buschmais.cdo.neo4j.impl.node.metadata.RelationshipMetadata;
import org.neo4j.graphdb.Node;

import java.util.Map;

public interface DatastoreSession<EntityId, Entity, RelationId, Relation> {

    // Transactions
    public interface DatastoreTransaction {

        void begin();

        void commit();

        void rollback();

        boolean isActive();
    }

    DatastoreTransaction getDatastoreTransaction();

    // Entities

    TypeSet getTypes(Entity entity);

    EntityId getId(Entity entity);

    Entity create(TypeSet types);

    void delete(Entity node);

    ResultIterator<Entity> find(Class<?> type, Object value);

    ResultIterator<Map<String, Object>> execute(String query, Map<String, Object> parameters);

    void migrate(Entity entity, TypeSet types, TypeSet targetTypes);

    //Properties
    void setProperty(Entity entity, PrimitivePropertyMethodMetadata metadata, Object value);

    boolean hasProperty(Entity entity, PrimitivePropertyMethodMetadata metadata);

    void removeProperty(Entity entity, PrimitivePropertyMethodMetadata metadata);

    Object getProperty(Entity entity, PrimitivePropertyMethodMetadata metadata);

    Enum<?> getEnumProperty(Entity entity, EnumPropertyMethodMetadata metadata);

    void setEnumProperty(Entity entity, EnumPropertyMethodMetadata metadata, Object value);

    // Relations
    boolean hasRelation(Entity source, RelationshipMetadata metadata, RelationshipMetadata.Direction direction);

    Relation getSingleRelation(Entity source, RelationshipMetadata metadata, RelationshipMetadata.Direction direction);

    Iterable<Relation> getRelations(Entity source, RelationshipMetadata metadata, RelationshipMetadata.Direction direction);

    Relation createRelation(Entity source, RelationshipMetadata metadata, RelationshipMetadata.Direction direction, Entity target);

    void deleteRelation(Relation relation);

    Entity getTarget(Relation relation);

    Entity getSource(Relation relation);
}
