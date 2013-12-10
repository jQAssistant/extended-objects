package com.buschmais.cdo.spi.datastore;

import com.buschmais.cdo.spi.metadata.EnumPropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.PrimitivePropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.RelationMetadata;

public interface DatastorePropertyManager<Entity, Relation, PrimitivePropertyDatastoreMetadata, EnumPropertyDatastoreMetadata, RelationDatastoreMetadata> {

    // Properties
    void setProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyDatastoreMetadata> metadata, Object value);

    boolean hasProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyDatastoreMetadata> metadata);

    void removeProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyDatastoreMetadata> metadata);

    Object getProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyDatastoreMetadata> metadata);

    // Enums
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
