package com.buschmais.xo.impl;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.spi.datastore.DatastorePropertyManager;
import com.buschmais.xo.spi.datastore.DatastoreRelationManager;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.xo.spi.datastore.DynamicType;
import com.buschmais.xo.spi.metadata.method.*;
import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class EntityPropertyManager<Entity, Relation, PropertyMetadata> extends AbstractPropertyManager<Entity> {

    private final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, PropertyMetadata> sessionContext;

    /**
     * Constructor.
     *
     * @param sessionContext
     *            The {@link SessionContext}.
     */
    public EntityPropertyManager(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, PropertyMetadata> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    protected DatastorePropertyManager<Entity, ?> getDatastorePropertyManager() {
        return sessionContext.getDatastoreSession().getDatastoreEntityManager();
    }

    @Override
    protected AbstractInstanceManager<?, Entity> getInstanceManager() {
        return sessionContext.getEntityInstanceManager();
    }

    public void createEntityReference(Entity sourceEntity, AbstractRelationPropertyMethodMetadata<?> metadata, Object target) {
        AbstractInstanceManager<?, Entity> instanceManager = sessionContext.getEntityInstanceManager();
        Entity targetEntity = target != null ? instanceManager.getDatastoreType(target) : null;
        createRelation(sourceEntity, metadata, targetEntity, null, Collections.emptyMap());
    }

    public <T> T createRelationReference(Entity sourceEntity, AbstractRelationPropertyMethodMetadata<?> fromProperty, Object target,
            AbstractRelationPropertyMethodMetadata<?> toProperty, Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> example) {
        AbstractInstanceManager<?, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
        if (target != null) {
            Entity targetEntity = entityInstanceManager.getDatastoreType(target);
            Relation relation = createRelation(sourceEntity, fromProperty, targetEntity, toProperty, example);
            AbstractInstanceManager<?, Relation> relationInstanceManager = sessionContext.getRelationInstanceManager();
            DynamicType<?> metadata = relationInstanceManager.getTypes(relation);
            return relationInstanceManager.createInstance(relation, metadata);
        }
        return null;
    }

    public Object getEntityReference(Entity entity, EntityReferencePropertyMethodMetadata metadata) {
        DatastoreRelationManager<Entity, ?, Relation, ?, ?, ?> relationManager = sessionContext.getDatastoreSession().getDatastoreRelationManager();
        if (relationManager.hasSingleRelation(entity, metadata.getRelationshipMetadata(), metadata.getDirection())) {
            Relation singleRelation = (Relation) relationManager.getSingleRelation(entity, metadata.getRelationshipMetadata(), metadata.getDirection());
            Entity target = getReferencedEntity(singleRelation, metadata.getDirection());
            return sessionContext.getEntityInstanceManager().readInstance(target);
        }
        return null;
    }

    public Iterator<Entity> getEntityCollection(Entity entity, final EntityCollectionPropertyMethodMetadata<?> metadata) {
        Iterable<Relation> relations = sessionContext.getDatastoreSession().getDatastoreRelationManager().getRelations(entity,
                metadata.getRelationshipMetadata(), metadata.getDirection());
        final Iterator<Relation> iterator = relations.iterator();
        return new Iterator<Entity>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Entity next() {
                Relation next = iterator.next();
                return getReferencedEntity(next, metadata.getDirection());
            }

            @Override
            public void remove() {
            }
        };
    }

    public Object getRelationReference(Entity entity, RelationReferencePropertyMethodMetadata<?> metadata) {
        DatastoreRelationManager<Entity, ?, Relation, ? extends DatastoreRelationMetadata<?>, ?, ?> relationManager = sessionContext.getDatastoreSession()
                .getDatastoreRelationManager();
        if (relationManager.hasSingleRelation(entity, metadata.getRelationshipMetadata(), metadata.getDirection())) {
            Relation singleRelation = (Relation) relationManager.getSingleRelation(entity, metadata.getRelationshipMetadata(), metadata.getDirection());
            return sessionContext.getRelationInstanceManager().readInstance(singleRelation);
        }
        return null;
    }

    public Iterator<Relation> getRelationCollection(Entity entity, RelationCollectionPropertyMethodMetadata<?> metadata) {
        Iterable<Relation> relations = sessionContext.getDatastoreSession().getDatastoreRelationManager().getRelations(entity,
                metadata.getRelationshipMetadata(), metadata.getDirection());
        return relations.iterator();
    }

    public void removeEntityReferences(Entity entity, EntityCollectionPropertyMethodMetadata metadata) {
        Iterable<Relation> relations = sessionContext.getDatastoreSession().getDatastoreRelationManager().getRelations(entity,
                metadata.getRelationshipMetadata(), metadata.getDirection());
        for (Relation relation : relations) {
            removeRelation(entity, relation, metadata);
        }
    }

    public boolean removeEntityReference(Entity entity, EntityCollectionPropertyMethodMetadata<?> metadata, Object target) {
        Iterable<Relation> relations = sessionContext.getDatastoreSession().getDatastoreRelationManager().getRelations(entity,
                metadata.getRelationshipMetadata(), metadata.getDirection());
        Entity targetEntity = sessionContext.getEntityInstanceManager().getDatastoreType(target);
        for (Relation relation : relations) {
            Entity referencedEntity = getReferencedEntity(relation, metadata.getDirection());
            if (referencedEntity.equals(targetEntity)) {
                removeRelation(entity, relation, metadata);
                return true;
            }
        }
        return false;
    }

    private void removeRelation(Entity source, Relation relation, AbstractRelationPropertyMethodMetadata<?> metadata) {
        AbstractInstanceManager<?, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
        entityInstanceManager.updateInstance(source);
        entityInstanceManager.updateInstance(getReferencedEntity(relation, metadata.getDirection()));
        sessionContext.getDatastoreSession().getDatastoreRelationManager().deleteRelation(relation);
        AbstractInstanceManager<?, Relation> relationInstanceManager = sessionContext.getRelationInstanceManager();
        if (metadata.getRelationshipMetadata().getAnnotatedType() != null) {
            Object instance = relationInstanceManager.readInstance(relation);
            relationInstanceManager.removeInstance(instance);
            relationInstanceManager.closeInstance(instance);
        }
    }

    private Entity getReferencedEntity(Relation relation, RelationTypeMetadata.Direction direction) {
        DatastoreRelationManager<Entity, ?, Relation, ? extends DatastoreRelationMetadata<?>, ?, ?> relationManager = sessionContext.getDatastoreSession()
                .getDatastoreRelationManager();
        switch (direction) {
        case FROM:
            return relationManager.getTo(relation);
        case TO:
            return relationManager.getFrom(relation);
        default:
            throw new XOException("Unsupported direction: " + direction);
        }
    }

    private Relation createRelation(Entity sourceEntity, AbstractRelationPropertyMethodMetadata<?> fromProperty, Entity targetEntity,
            AbstractRelationPropertyMethodMetadata<?> toProperty, Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> example) {
        Relation relation;
        if (fromProperty instanceof EntityReferencePropertyMethodMetadata || fromProperty instanceof RelationReferencePropertyMethodMetadata) {
            relation = createSingleReference(sourceEntity, fromProperty, targetEntity, example);
        } else if (toProperty instanceof EntityReferencePropertyMethodMetadata || toProperty instanceof RelationReferencePropertyMethodMetadata) {
            relation = createSingleReference(targetEntity, toProperty, sourceEntity, example);
        } else if (fromProperty instanceof EntityCollectionPropertyMethodMetadata || fromProperty instanceof RelationCollectionPropertyMethodMetadata) {
            relation = createReference(sourceEntity, fromProperty.getRelationshipMetadata(), fromProperty.getDirection(), targetEntity, example);
        } else {
            throw new XOException("Unsupported relation type " + fromProperty.getClass().getName());
        }
        return relation;
    }

    private Relation createSingleReference(Entity sourceEntity, AbstractRelationPropertyMethodMetadata<?> metadata, Entity targetEntity,
            Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> example) {
        DatastoreRelationManager<Entity, ?, Relation, ? extends DatastoreRelationMetadata<?>, ?, PropertyMetadata> relationManager = sessionContext
                .getDatastoreSession().getDatastoreRelationManager();
        AbstractInstanceManager<?, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
        if (relationManager.hasSingleRelation(sourceEntity, metadata.getRelationshipMetadata(), metadata.getDirection())) {
            Relation relation = (Relation) relationManager.getSingleRelation(sourceEntity, metadata.getRelationshipMetadata(), metadata.getDirection());
            Entity referencedEntity = getReferencedEntity(relation, metadata.getDirection());
            entityInstanceManager.updateInstance(referencedEntity);
            removeRelation(sourceEntity, relation, metadata);
        }
        entityInstanceManager.updateInstance(sourceEntity);
        if (targetEntity != null) {
            Relation relation = (Relation) relationManager.createRelation(sourceEntity, metadata.getRelationshipMetadata(), metadata.getDirection(),
                    targetEntity, example);
            entityInstanceManager.updateInstance(targetEntity);
            return relation;
        }
        return null;
    }

    private Relation createReference(Entity sourceEntity, RelationTypeMetadata metadata, RelationTypeMetadata.Direction direction, Entity targetEntity,
            Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> example) {
        DatastoreRelationManager<Entity, ?, Relation, ?, ?, PropertyMetadata> datastoreRelationManager = sessionContext.getDatastoreSession()
                .getDatastoreRelationManager();
        Relation relation = (Relation) datastoreRelationManager.createRelation(sourceEntity, metadata, direction, targetEntity, example);
        AbstractInstanceManager<?, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
        entityInstanceManager.updateInstance(sourceEntity);
        entityInstanceManager.updateInstance(targetEntity);
        return relation;
    }

}
