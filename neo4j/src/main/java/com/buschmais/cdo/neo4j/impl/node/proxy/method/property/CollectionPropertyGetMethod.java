package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.neo4j.impl.common.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.CollectionPropertyMethodMetadata;
import com.buschmais.cdo.neo4j.impl.node.proxy.collection.CollectionProxy;
import com.buschmais.cdo.neo4j.impl.node.proxy.collection.ListProxy;
import com.buschmais.cdo.neo4j.impl.node.proxy.collection.SetProxy;
import com.buschmais.cdo.neo4j.impl.common.PropertyManager;
import org.neo4j.graphdb.Node;

import java.util.List;
import java.util.Set;

public class CollectionPropertyGetMethod extends AbstractPropertyMethod<CollectionPropertyMethodMetadata> {

    public CollectionPropertyGetMethod(CollectionPropertyMethodMetadata metadata, InstanceManager instanceManager, PropertyManager propertyManager) {
        super(metadata, instanceManager, propertyManager);
    }

    @Override
    public Object invoke(Node entity, Object instance, Object[] args) {
        CollectionPropertyMethodMetadata collectionPropertyMetadata = getMetadata();
        CollectionProxy<?> collectionProxy = new CollectionProxy<>(entity, getMetadata().getRelationshipMetadata(), getMetadata().getDirection(), getInstanceManager(), getPropertyManager());
        if (Set.class.isAssignableFrom(collectionPropertyMetadata.getBeanMethod().getType())) {
            return new SetProxy<>(collectionProxy);
        } else if (List.class.isAssignableFrom(collectionPropertyMetadata.getBeanMethod().getType())) {
            return new ListProxy<>(collectionProxy);
        }
        throw new CdoException("Unsupported collection type " + collectionPropertyMetadata.getBeanMethod().getType());
    }
}
