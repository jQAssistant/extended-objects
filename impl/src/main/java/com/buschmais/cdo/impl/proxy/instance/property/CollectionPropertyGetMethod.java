package com.buschmais.cdo.impl.proxy.instance.property;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.impl.proxy.collection.CollectionProxy;
import com.buschmais.cdo.impl.proxy.collection.ListProxy;
import com.buschmais.cdo.impl.proxy.collection.SetProxy;
import com.buschmais.cdo.impl.proxy.interceptor.InterceptorFactory;
import com.buschmais.cdo.impl.proxy.interceptor.TransactionInterceptor;
import com.buschmais.cdo.spi.metadata.CollectionPropertyMethodMetadata;

import javax.swing.event.InternalFrameEvent;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.buschmais.cdo.api.CdoManagerFactory.TransactionAttribute;

public class CollectionPropertyGetMethod<Entity> extends AbstractPropertyMethod<Entity, CollectionPropertyMethodMetadata> {

    private CdoTransaction cdoTransaction;

    private TransactionAttribute transactionAttribute;

    public CollectionPropertyGetMethod(CollectionPropertyMethodMetadata<?> metadata, InstanceManager instanceManager, PropertyManager propertyManager, CdoTransaction cdoTransaction, TransactionAttribute transactionAttribute) {
        super(metadata, instanceManager, propertyManager);
        this.cdoTransaction = cdoTransaction;
        this.transactionAttribute = transactionAttribute;
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        CollectionPropertyMethodMetadata<?> collectionPropertyMetadata = getMetadata();
        CollectionProxy<?, Entity> collectionProxy = new CollectionProxy<>(entity, getMetadata().getRelationshipMetadata(), getMetadata().getDirection(), getInstanceManager(), getPropertyManager());
        Collection<?> collection;
        if (Set.class.isAssignableFrom(collectionPropertyMetadata.getBeanMethod().getType())) {
            collection = new SetProxy<>(collectionProxy);
        } else if (List.class.isAssignableFrom(collectionPropertyMetadata.getBeanMethod().getType())) {
            collection = new ListProxy<>(collectionProxy);
        } else {
        throw new CdoException("Unsupported collection type " + collectionPropertyMetadata.getBeanMethod().getType());
        }
        Collection<?> result = InterceptorFactory.addInterceptor(collection, new TransactionInterceptor(collection, cdoTransaction, transactionAttribute));
        return result;
    }
}
