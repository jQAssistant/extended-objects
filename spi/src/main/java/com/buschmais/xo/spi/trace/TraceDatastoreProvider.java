package com.buschmais.xo.spi.trace;

import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.spi.bootstrap.XODatastoreProvider;
import com.buschmais.xo.spi.datastore.Datastore;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.xo.spi.reflection.ClassHelper;

import java.util.Properties;

/**
 * {@link XODatastoreProvider} implementation allowing tracing on delegates.
 */
public class TraceDatastoreProvider<EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> implements XODatastoreProvider<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> {

    @Override
    public Datastore<?, EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> createDatastore(XOUnit xoUnit) {
        Properties properties = xoUnit.getProperties();
        String delegateProviderName = properties.getProperty(TraceDatastoreProvider.class.getPackage().getName() + ".DelegateProvider");
        Class<XODatastoreProvider> delegateProviderType = ClassHelper.getType(delegateProviderName);
        XODatastoreProvider<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> delegateProvider = ClassHelper.newInstance(delegateProviderType);
        Datastore<?, EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> delegateDatastore = delegateProvider.createDatastore(xoUnit);
        return new TraceDatastore<>(delegateDatastore);
    }
}
