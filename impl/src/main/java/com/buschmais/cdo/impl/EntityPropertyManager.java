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

    public <T> T createEntityReference(Entity sourceEntity, AbstractRelationPropertyMethodMetadata<?> metadata, Object target) {
        AbstractInstanceManager<?, Entity> instanceManager = getSessionContext().getEntityInstanceManager();
        Entity targetEntity = target != null ? instanceManager.getDatastoreType(target) : null;
        Relation relation = createRelation(sourceEntity, metadata, targetEntity, null);
        return relation != null ? (T) instanceManager.getInstance(getReferencedEntity(relation, metadata.getDirection())) : null;
    }

    public <T> T createRelationReference(Entity sourceEntity, AbstractRelationPropertyMethodMetadata<?> fromProperty, Object target, AbstractRelationPropertyMethodMetadata<?> toProperty) {
        AbstractInstanceManager<?, Entity> entityInstanceManager = getSessionContext().getEntityInstanceManager();
        Entity targetEntity = target != null ? entityInstanceManager.getDatastoreType(target) : null;
        Relation relation = createRelation(sourceEntity, fromProperty, targetEntity, toProperty);
        AbstractInstanceManager<?, Relation> relationInstanceManager = getSessionContext().getRelationInstanceManager();
        return relation != null ? (T) relationInstanceManager.getInstance(relation) : null;
    }

    @Override
    public void setProperty(Entity entity, PrimitivePropertyMethodMetadata metadata, Object value) {
        SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext = getSessionContext();
        sessionContext.getDatastoreSession().getDatastorePropertyManager().setEntityProperty(entity, metadata, value);
    }

    @Override
    public Object getProperty(Entity entity, PrimitivePropertyMethodMetadata metadata) {
        return getSessionContext().getDatastoreSession().getDatastorePropertyManager().getProperty(entity, metadata);
    }

    @Override
    public boolean hasProperty(Entity entity, PrimitivePropertyMethodMetadata metadata) {
        return getSessionContext().getDatastoreSession().getDatastorePropertyManager().hasProperty(entity, metadata);
    }

    @Override
    public void removeProperty(Entity entity, PrimitivePropertyMethodMetadata metadata) {
        getSessionContext().getDatastoreSession().getDatastorePropertyManager().removeProperty(entity, metadata);
    }

    @Override
    public void setEnumProperty(Entity entity, EnumPropertyMethodMetadata metadata, Enum<?> value) {
        getSessionContext().getDatastoreSession().getDatastorePropertyManager().setEnumProperty(entity, metadata, value);
    }

    @Override
    public Enum<?> getEnumProperty(Entity entity, EnumPropertyMethodMetadata metadata) {
        return getSessionContext().getDatastoreSession().getDatastorePropertyManager().getEnumProperty(entity, metadata);
    }

    public Object getEntityReference(Entity entity, EntityReferencePropertyMethodMetadata metadata) {
        Relation singleRelation = getSingleRelation(entity, metadata.getRelationshipMetadata(), metadata.getDirection());
        if (singleRelation != null) {
            Entity target = getReferencedEntity(singleRelation, metadata.getDirection());
            return getSessionContext().getEntityInstanceManager().getInstance(target);
        }
        return null;
    }

    public void removeEntityReferences(Entity entity, EntityCollectionPropertyMethodMetadata metadata) {
        removeRelations(entity, metadata.getRelationshipMetadata(), metadata.getDirection());
    }

    public Iterator<Entity> getEntityCollection(Entity entity, EntityCollectionPropertyMethodMetadata<?> metadata) {
        return getRelations(entity, metadata.getRelationshipMetadata(), metadata.getDirection());
    }

    public Iterator<Relation> getRelationCollection(Entity entity, RelationCollectionPropertyMethodMetadata<?> metadata) {
        Iterable<Relation> relations = getSessionContext().getDatastoreSession().getDatastorePropertyManager().getRelations(entity, metadata.getRelationshipMetadata(), metadata.getDirection());
        return relations.iterator();
    }

    public boolean removeEntityReference(Entity entity, EntityCollectionPropertyMethodMetadata<?> metadata, Object target) {
        Entity targetEntity = getSessionContext().getEntityInstanceManager().getDatastoreType(target);
        return removeEntityRelation(entity, metadata.getRelationshipMetadata(), metadata.getDirection(), targetEntity);
    }

    public Object getRelationReference(Entity entity, RelationReferencePropertyMethodMetadata<?> metadata) {
        Relation singleRelation = getSingleRelation(entity, metadata.getRelationshipMetadata(), metadata.getDirection());
        if (singleRelation != null) {
            return getSessionContext().getRelationInstanceManager().getInstance(singleRelation);
        }
        return null;
    }

    /**
     * Remove an existing relationship.
     *
     * @param source The node.
     * @param target The target node.
     * @return <code>true</code> if an existing relationship has been removed.
     */
    private boolean removeEntityRelation(Entity source, RelationTypeMetadata metadata, RelationTypeMetadata.Direction direction, Entity target) {
        DatastorePropertyManager<Entity, Relation, ?, ?, ? extends DatastoreRelationMetadata<?>> datastorePropertyManager = getSessionContext().getDatastoreSession().getDatastorePropertyManager();
        Iterable<Relation> relations = datastorePropertyManager.getRelations(source, metadata, direction);
        for (Relation relation : relations) {
            if (getReferencedEntity(relation, direction).equals(target)) {
                datastorePropertyManager.deleteRelation(relation);
                return true;
            }
        }
        return false;
    }

    /**
     * Return all relationships of a node.
     *
     * @param source The node.
     * @return An iterator delivering all target nodes.
     */
    private Iterator<Entity> getRelations(Entity source, RelationTypeMetadata metadata, final RelationTypeMetadata.Direction direction) {
        Iterable<Relation> relations = getSessionContext().getDatastoreSession().getDatastorePropertyManager().getRelations(source, metadata, direction);
        final Iterator<Relation> iterator = relations.iterator();
        return new Iterator<Entity>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Entity next() {
                Relation next = iterator.next();
                return getReferencedEntity(next, direction);
            }

            @Override
            public void remove() {
            }
        };
    }

    private Entity getReferencedEntity(Relation relation, RelationTypeMetadata.Direction direction) {
        DatastorePropertyManager<Entity, Relation, ?, ?, ? extends DatastoreRelationMetadata<?>> datastorePropertyManager = getSessionContext().getDatastoreSession().getDatastorePropertyManager();
        switch (direction) {
            case OUTGOING:
                return datastorePropertyManager.getTarget(relation);
            case INCOMING:
                return datastorePropertyManager.getSource(relation);
            default:
                throw new CdoException("Unsupported direction: " + direction);
        }
    }

    /**
     * Remove all relationships of a entity.
     *
     * @param source The entity.
     */
    private void removeRelations(Entity source, RelationTypeMetadata metadata, RelationTypeMetadata.Direction direction) {
        DatastorePropertyManager<Entity, Relation, ?, ?, ? extends DatastoreRelationMetadata<?>> datastorePropertyManager = getSessionContext().getDatastoreSession().getDatastorePropertyManager();
        Iterable<Relation> relations = datastorePropertyManager.getRelations(source, metadata, direction);
        for (Relation relation : relations) {
            datastorePropertyManager.deleteRelation(relation);
        }
    }

    private Relation createRelation(Entity sourceEntity, AbstractRelationPropertyMethodMetadata<?> fromProperty, Entity targetEntity, AbstractRelationPropertyMethodMetadata<?> toProperty) {
        Relation relation;
        if (fromProperty instanceof EntityReferencePropertyMethodMetadata || fromProperty instanceof RelationReferencePropertyMethodMetadata) {
            relation = createSingleReference(sourceEntity, fromProperty.getRelationshipMetadata(), fromProperty.getDirection(), targetEntity);
        } else if (toProperty instanceof EntityReferencePropertyMethodMetadata || toProperty instanceof RelationReferencePropertyMethodMetadata) {
            relation = createSingleReference(targetEntity, toProperty.getRelationshipMetadata(), toProperty.getDirection(), sourceEntity);
        } else if (fromProperty instanceof EntityCollectionPropertyMethodMetadata || fromProperty instanceof RelationCollectionPropertyMethodMetadata) {
            relation = createReference(sourceEntity, fromProperty.getRelationshipMetadata(), fromProperty.getDirection(), targetEntity);
        } else {
            throw new CdoException("Unsupported relation type " + fromProperty.getClass().getName());
        }
        return relation;
    }

    private Relation createSingleReference(Entity sourceEntity, RelationTypeMetadata metadata, RelationTypeMetadata.Direction direction, Entity targetEntity) {
        DatastorePropertyManager<Entity, Relation, ?, ?, ?> datastorePropertyManager = getSessionContext().getDatastoreSession().getDatastorePropertyManager();
        if (datastorePropertyManager.hasSingleRelation(sourceEntity, metadata, direction)) {
            Relation relation = datastorePropertyManager.getSingleRelation(sourceEntity, metadata, direction);
            datastorePropertyManager.deleteRelation(relation);
            AbstractInstanceManager<?, Relation> relationInstanceManager = getSessionContext().getRelationInstanceManager();
            if (metadata.getAnnotatedType() != null) {
                Object instance = relationInstanceManager.getInstance(relation);
                relationInstanceManager.removeInstance(instance);
                relationInstanceManager.destroyInstance(instance);
            }
        }
        return targetEntity != null ? datastorePropertyManager.createRelation(sourceEntity, metadata, direction, targetEntity) : null;
    }

    private Relation createReference(Entity sourceEntity, RelationTypeMetadata metadata, RelationTypeMetadata.Direction direction, Entity targetEntity) {
        DatastorePropertyManager<Entity, Relation, ?, ?, ?> datastorePropertyManager = getSessionContext().getDatastoreSession().getDatastorePropertyManager();
        return datastorePropertyManager.createRelation(sourceEntity, metadata, direction, targetEntity);
    }
}
