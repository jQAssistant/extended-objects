package com.buschmais.cdo.impl.proxy.instance.property;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.impl.proxy.collection.CollectionProxy;
import com.buschmais.cdo.impl.proxy.collection.ListProxy;
import com.buschmais.cdo.impl.proxy.collection.SetProxy;
import com.buschmais.cdo.spi.metadata.CollectionPropertyMethodMetadata;

import java.util.List;
import java.util.Set;

public class CollectionPropertyGetMethod<Entity> extends AbstractPropertyMethod<Entity, CollectionPropertyMethodMetadata> {

    public CollectionPropertyGetMethod(CollectionPropertyMethodMetadata<?> metadata, InstanceManager instanceManager, PropertyManager propertyManager) {
        super(metadata, instanceManager, propertyManager);
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        CollectionPropertyMethodMetadata<?> collectionPropertyMetadata = getMetadata();
        CollectionProxy<?, Entity> collectionProxy = new CollectionProxy<>(entity, getMetadata().getRelationshipMetadata(), getMetadata().getDirection(), getInstanceManager(), getPropertyManager());
        if (Set.class.isAssignableFrom(collectionPropertyMetadata.getBeanMethod().getType())) {
            return new SetProxy<>(collectionProxy);
        } else if (List.class.isAssignableFrom(collectionPropertyMetadata.getBeanMethod().getType())) {
            return new ListProxy<>(collectionProxy);
        }
        throw new CdoException("Unsupported collection type " + collectionPropertyMetadata.getBeanMethod().getType());
    }
}
