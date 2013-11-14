package com.buschmais.cdo.neo4j.impl.proxy.method;

import com.buschmais.cdo.api.CdoManagerException;
import com.buschmais.cdo.neo4j.impl.metadata.CollectionMethodMetadata;
import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import com.buschmais.cdo.neo4j.impl.proxy.collection.CollectionProxy;
import com.buschmais.cdo.neo4j.impl.proxy.collection.ListProxy;
import com.buschmais.cdo.neo4j.impl.proxy.collection.SetProxy;
import org.neo4j.graphdb.Node;

import java.util.List;
import java.util.Set;

public class CollectionPropertyGetMethod extends AbstractPropertyMethod<CollectionMethodMetadata> {

    public CollectionPropertyGetMethod(CollectionMethodMetadata metadata, InstanceManager instanceManager) {
        super(metadata, instanceManager);
    }

    @Override
    public Object invoke(Node node, Object instance, Object[] args) {
        CollectionMethodMetadata collectionPropertyMetadata = getMetadata();
        CollectionProxy<?> collectionProxy = new CollectionProxy<>(node, collectionPropertyMetadata.getRelationshipType(), getInstanceManager());
        if (Set.class.isAssignableFrom(collectionPropertyMetadata.getBeanMethod().getType())) {
            return new SetProxy<>(collectionProxy);
        } else if (List.class.isAssignableFrom(collectionPropertyMetadata.getBeanMethod().getType())) {
            return new ListProxy<>(collectionProxy);
        }
        throw new CdoManagerException("Unsupported collection type " + collectionPropertyMetadata.getBeanMethod().getType());
    }
}
