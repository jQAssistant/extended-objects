package com.buschmais.cdo.neo4j.spi;

import com.buschmais.cdo.api.ResultIterator;
import com.buschmais.cdo.neo4j.impl.node.metadata.EnumPropertyMethodMetadata;
import com.buschmais.cdo.neo4j.impl.node.metadata.PrimitivePropertyMethodMetadata;
import com.buschmais.cdo.neo4j.impl.node.metadata.RelationshipMetadata;
import org.neo4j.graphdb.Node;

import java.util.Map;

public interface DatastoreSession<EntityId, Entity, RelationId, Relation, PrimitivePropertyDatastoreMetadata, EnumPropertyDatastoreMetadata, RelationDatastoreMetadata> {

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
    void setProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyDatastoreMetadata> metadata, Object value);

    boolean hasProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyDatastoreMetadata> metadata);

    void removeProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyDatastoreMetadata> metadata);

    Object getProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyDatastoreMetadata> metadata);

    Enum<?> getEnumProperty(Entity entity, EnumPropertyMethodMetadata<EnumPropertyDatastoreMetadata> metadata);

    void setEnumProperty(Entity entity, EnumPropertyMethodMetadata<EnumPropertyDatastoreMetadata> metadata, Object value);

    // Relations
    boolean hasRelation(Entity source, RelationshipMetadata<RelationDatastoreMetadata> metadata, RelationshipMetadata.Direction direction);

    Relation getSingleRelation(Entity source, RelationshipMetadata<RelationDatastoreMetadata> metadata, RelationshipMetadata.Direction direction);

    Iterable<Relation> getRelations(Entity source, RelationshipMetadata<RelationDatastoreMetadata> metadata, RelationshipMetadata.Direction direction);

    Relation createRelation(Entity source, RelationshipMetadata<RelationDatastoreMetadata> metadata, RelationshipMetadata.Direction direction, Entity target);

    void deleteRelation(Relation relation);

    Entity getTarget(Relation relation);

    Entity getSource(Relation relation);
}
