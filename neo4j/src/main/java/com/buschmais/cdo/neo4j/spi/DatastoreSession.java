package com.buschmais.cdo.neo4j.spi;

import com.buschmais.cdo.api.Query;
import com.buschmais.cdo.api.ResultIterator;
import com.buschmais.cdo.neo4j.impl.node.metadata.EnumPropertyMethodMetadata;
import com.buschmais.cdo.neo4j.impl.node.metadata.PrimitivePropertyMethodMetadata;
import com.buschmais.cdo.neo4j.impl.node.metadata.RelationMetadata;

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

    <QL> ResultIterator<Map<String, Object>> execute(QL query, Map<String, Object> parameters);

    void migrate(Entity entity, TypeSet types, TypeSet targetTypes);

    //Properties
    void setProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyDatastoreMetadata> metadata, Object value);

    boolean hasProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyDatastoreMetadata> metadata);

    void removeProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyDatastoreMetadata> metadata);

    Object getProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyDatastoreMetadata> metadata);

    Enum<?> getEnumProperty(Entity entity, EnumPropertyMethodMetadata<EnumPropertyDatastoreMetadata> metadata);

    void setEnumProperty(Entity entity, EnumPropertyMethodMetadata<EnumPropertyDatastoreMetadata> metadata, Object value);

    // Relations
    boolean hasRelation(Entity source, RelationMetadata<RelationDatastoreMetadata> metadata, RelationMetadata.Direction direction);

    Relation getSingleRelation(Entity source, RelationMetadata<RelationDatastoreMetadata> metadata, RelationMetadata.Direction direction);

    Iterable<Relation> getRelations(Entity source, RelationMetadata<RelationDatastoreMetadata> metadata, RelationMetadata.Direction direction);

    Relation createRelation(Entity source, RelationMetadata<RelationDatastoreMetadata> metadata, RelationMetadata.Direction direction, Entity target);

    void deleteRelation(Relation relation);

    Entity getTarget(Relation relation);

    Entity getSource(Relation relation);
}
