package com.buschmais.xo.trace.api;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.spi.bootstrap.XODatastoreProvider;
import com.buschmais.xo.spi.datastore.Datastore;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.xo.spi.interceptor.InterceptorFactory;
import com.buschmais.xo.spi.reflection.ClassHelper;
import com.buschmais.xo.trace.impl.TraceMonitor;
import com.buschmais.xo.trace.impl.TraceDatastore;
import com.buschmais.xo.trace.impl.TraceMonitorInterceptor;

import java.util.Arrays;
import java.util.Properties;

/**
 * {@link XODatastoreProvider} implementation allowing tracing on delegates.
 */
public class TraceDatastoreProvider<EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> implements XODatastoreProvider<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> {

    public static final String DELEGATE_KEY = TraceDatastoreProvider.class.getPackage().getName() + ".DelegateProvider";

    @Override
    public Datastore<?, EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> createDatastore(XOUnit xoUnit) {
        Properties properties = xoUnit.getProperties();
        String delegateProviderName = properties.getProperty(DELEGATE_KEY);
        if (delegateProviderName == null) {
            throw new XOException("Property '" + DELEGATE_KEY + "' must be specified using the class name of a data store provider.");
        }
        Class<XODatastoreProvider> delegateProviderType = ClassHelper.getType(delegateProviderName);
        XODatastoreProvider<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> delegateProvider = ClassHelper.newInstance(delegateProviderType);
        Datastore<?, EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> delegateDatastore = delegateProvider.createDatastore(xoUnit);
        TraceMonitor traceMonitor = new TraceMonitor(xoUnit);
        InterceptorFactory interceptorFactory = new InterceptorFactory(Arrays.asList(new TraceMonitorInterceptor(traceMonitor)));
        return new TraceDatastore<>(interceptorFactory.addInterceptor(delegateDatastore, Datastore.class), interceptorFactory, traceMonitor);
    }
 }
