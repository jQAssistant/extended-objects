package com.buschmais.cdo.impl;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.spi.datastore.DatastorePropertyManager;
import com.buschmais.cdo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.cdo.spi.metadata.method.*;
import com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata;

import java.util.Iterator;

/**
 * Contains methods for reading and creating relationships specified by the given metadata.
 * <p/>
 * <p>For each provided method the direction of the relationships is handled transparently for the caller.</p>
 */
public class EntityPropertyManager<Entity, Relation> extends AbstractPropertyManager<Entity, Entity, Relation> {

    /**
     * Constructor.
     *
     * @param sessionContext The {@link SessionContext}.
     */
    public EntityPropertyManager(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext) {
        super(sessionContext);
    }

    @Override
    public void setProperty(Entity entity, PrimitivePropertyMethodMetadata metadata, Object value) {
        SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext = getSessionContext();
        sessionContext.getDatastoreSession().getDatastorePropertyManager().setEntityProperty(entity, metadata, value);
        sessionContext.getEntityInstanceManager().writeInstance(entity);
    }

    @Override
    public Object getProperty(Entity entity, PrimitivePropertyMethodMetadata metadata) {
        return getSessionContext().getDatastoreSession().getDatastorePropertyManager().getEntityProperty(entity, metadata);
    }

    @Override
    public boolean hasProperty(Entity entity, PrimitivePropertyMethodMetadata metadata) {
        return getSessionContext().getDatastoreSession().getDatastorePropertyManager().hasEntityProperty(entity, metadata);
    }

    @Override
    public void removeProperty(Entity entity, PrimitivePropertyMethodMetadata metadata) {
        SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext = getSessionContext();
        sessionContext.getDatastoreSession().getDatastorePropertyManager().removeEntityProperty(entity, metadata);
        sessionContext.getEntityInstanceManager().writeInstance(entity);
    }

    public <T> T createEntityReference(Entity sourceEntity, AbstractRelationPropertyMethodMetadata<?> metadata, Object target) {
        AbstractInstanceManager<?, Entity> instanceManager = getSessionContext().getEntityInstanceManager();
        Entity targetEntity = target != null ? instanceManager.getDatastoreType(target) : null;
        Relation relation = createRelation(sourceEntity, metadata, targetEntity, null);
        return relation != null ? (T) instanceManager.writeInstance(getReferencedEntity(relation, metadata.getDirection())) : null;
    }

    public <T> T createRelationReference(Entity sourceEntity, AbstractRelationPropertyMethodMetadata<?> fromProperty, Object target, AbstractRelationPropertyMethodMetadata<?> toProperty) {
        SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext = getSessionContext();
        AbstractInstanceManager<?, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
        if (target != null) {
            Entity targetEntity = entityInstanceManager.getDatastoreType(target);
            Relation relation = createRelation(sourceEntity, fromProperty, targetEntity, toProperty);
            entityInstanceManager.writeInstance(targetEntity);
            return sessionContext.getRelationInstanceManager().writeInstance(relation);
        }
        return null;
    }

    public Object getEntityReference(Entity entity, EntityReferencePropertyMethodMetadata metadata) {
        Relation singleRelation = getSingleRelation(entity, metadata.getRelationshipMetadata(), metadata.getDirection());
        if (singleRelation != null) {
            Entity target = getReferencedEntity(singleRelation, metadata.getDirection());
            return getSessionContext().getEntityInstanceManager().readInstance(target);
        }
        return null;
    }

    public Iterator<Entity> getEntityCollection(Entity entity, final EntityCollectionPropertyMethodMetadata<?> metadata) {
        Iterable<Relation> relations = getSessionContext().getDatastoreSession().getDatastorePropertyManager().getRelations(entity, metadata.getRelationshipMetadata(), metadata.getDirection());
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
        Relation singleRelation = getSingleRelation(entity, metadata.getRelationshipMetadata(), metadata.getDirection());
        if (singleRelation != null) {
            return getSessionContext().getRelationInstanceManager().readInstance(singleRelation);
        }
        return null;
    }

    public Iterator<Relation> getRelationCollection(Entity entity, RelationCollectionPropertyMethodMetadata<?> metadata) {
        Iterable<Relation> relations = getSessionContext().getDatastoreSession().getDatastorePropertyManager().getRelations(entity, metadata.getRelationshipMetadata(), metadata.getDirection());
        return relations.iterator();
    }

    public void removeEntityReferences(Entity entity, EntityCollectionPropertyMethodMetadata metadata) {
        Iterable<Relation> relations = getSessionContext().getDatastoreSession().getDatastorePropertyManager().getRelations(entity, metadata.getRelationshipMetadata(), metadata.getDirection());
        for (Relation relation : relations) {
            removeRelation(entity, relation, metadata);
        }
    }

    public boolean removeEntityReference(Entity entity, EntityCollectionPropertyMethodMetadata<?> metadata, Object target) {
        Iterable<Relation> relations = getSessionContext().getDatastoreSession().getDatastorePropertyManager().getRelations(entity, metadata.getRelationshipMetadata(), metadata.getDirection());
        Entity targetEntity = getSessionContext().getEntityInstanceManager().getDatastoreType(target);
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
        SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext = getSessionContext();
        DatastorePropertyManager<Entity, Relation, ?, ? extends DatastoreRelationMetadata<?>> datastorePropertyManager = sessionContext.getDatastoreSession().getDatastorePropertyManager();
        AbstractInstanceManager<?, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
        entityInstanceManager.writeInstance(source);
        entityInstanceManager.writeInstance(getReferencedEntity(relation, metadata.getDirection()));
        datastorePropertyManager.deleteRelation(relation);
        AbstractInstanceManager<?, Relation> relationInstanceManager = getSessionContext().getRelationInstanceManager();
        if (metadata.getRelationshipMetadata().getAnnotatedType() != null) {
            Object instance = relationInstanceManager.readInstance(relation);
            relationInstanceManager.removeInstance(instance);
            relationInstanceManager.destroyInstance(instance);
        }

    }

    private Entity getReferencedEntity(Relation relation, RelationTypeMetadata.Direction direction) {
        DatastorePropertyManager<Entity, Relation, ?, ? extends DatastoreRelationMetadata<?>> datastorePropertyManager = getSessionContext().getDatastoreSession().getDatastorePropertyManager();
        switch (direction) {
            case FROM:
                return datastorePropertyManager.getTo(relation);
            case TO:
                return datastorePropertyManager.getFrom(relation);
            default:
                throw new CdoException("Unsupported direction: " + direction);
        }
    }

    private Relation createRelation(Entity sourceEntity, AbstractRelationPropertyMethodMetadata<?> fromProperty, Entity targetEntity, AbstractRelationPropertyMethodMetadata<?> toProperty) {
        Relation relation;
        if (fromProperty instanceof EntityReferencePropertyMethodMetadata || fromProperty instanceof RelationReferencePropertyMethodMetadata) {
            relation = createSingleReference(sourceEntity, fromProperty, targetEntity);
        } else if (toProperty instanceof EntityReferencePropertyMethodMetadata || toProperty instanceof RelationReferencePropertyMethodMetadata) {
            relation = createSingleReference(targetEntity, toProperty, sourceEntity);
        } else if (fromProperty instanceof EntityCollectionPropertyMethodMetadata || fromProperty instanceof RelationCollectionPropertyMethodMetadata) {
            relation = createReference(sourceEntity, fromProperty.getRelationshipMetadata(), fromProperty.getDirection(), targetEntity);
        } else {
            throw new CdoException("Unsupported relation type " + fromProperty.getClass().getName());
        }
        return relation;
    }

    private Relation createSingleReference(Entity sourceEntity, AbstractRelationPropertyMethodMetadata<?> metadata, Entity targetEntity) {
        DatastorePropertyManager<Entity, Relation, ?, ?> datastorePropertyManager = getSessionContext().getDatastoreSession().getDatastorePropertyManager();
        if (datastorePropertyManager.hasSingleRelation(sourceEntity, metadata.getRelationshipMetadata(), metadata.getDirection())) {
            Relation relation = datastorePropertyManager.getSingleRelation(sourceEntity, metadata.getRelationshipMetadata(), metadata.getDirection());
            removeRelation(sourceEntity, relation, metadata);
        }
        return targetEntity != null ? datastorePropertyManager.createRelation(sourceEntity, metadata.getRelationshipMetadata(), metadata.getDirection(), targetEntity) : null;
    }

    private Relation createReference(Entity sourceEntity, RelationTypeMetadata metadata, RelationTypeMetadata.Direction direction, Entity targetEntity) {
        DatastorePropertyManager<Entity, Relation, ?, ?> datastorePropertyManager = getSessionContext().getDatastoreSession().getDatastorePropertyManager();
        return datastorePropertyManager.createRelation(sourceEntity, metadata, direction, targetEntity);
    }
}
