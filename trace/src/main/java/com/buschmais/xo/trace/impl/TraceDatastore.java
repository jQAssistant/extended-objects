package com.buschmais.xo.trace.impl;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.spi.datastore.Datastore;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.DatastoreMetadataFactory;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.xo.spi.interceptor.InterceptorFactory;
import com.buschmais.xo.spi.metadata.type.TypeMetadata;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Map;

/**
 * {@link Datastore} implementation allowing tracing on delegates.
 */
public class TraceDatastore<DatastoreSession extends com.buschmais.xo.spi.datastore.DatastoreSession, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> implements Datastore<TraceDatastoreSession, EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> {

    private Datastore<DatastoreSession, EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> delegate;

    private InterceptorFactory interceptorFactory;

    private TraceMonitor traceMonitor;

    public TraceDatastore(Datastore<DatastoreSession, EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> delegate, InterceptorFactory interceptorFactory, TraceMonitor traceMonitor) {
        this.delegate = delegate;
        this.interceptorFactory = interceptorFactory;
        this.traceMonitor = traceMonitor;
    }

    @Override
    public void init(Map<Class<?>, TypeMetadata> registeredMetadata) {
        ObjectName objectName = getObjectName();
        try {
            getMBeanServer().registerMBean(traceMonitor, objectName);
        } catch (JMException e) {
            throw new XOException("Cannot register trace monitor MBean for object name " + objectName, e);
        }
        delegate.init(registeredMetadata);
    }

    @Override
    public DatastoreMetadataFactory<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> getMetadataFactory() {
        return delegate.getMetadataFactory();
    }

    @Override
    public TraceDatastoreSession createSession() {
        DatastoreSession delegateSession = delegate.createSession();
        return new TraceDatastoreSession(interceptorFactory.addInterceptor(delegateSession, com.buschmais.xo.spi.datastore.DatastoreSession.class), interceptorFactory);
    }

    @Override
    public void close() {
        delegate.close();
        ObjectName objectName = getObjectName();
        try {
            getMBeanServer().unregisterMBean(objectName);
        } catch (JMException e) {
            throw new XOException("Cannot unregister trace monitor MBean for object name " + objectName, e);
        }
    }

    private MBeanServer getMBeanServer() {
        return ManagementFactory.getPlatformMBeanServer();
    }

    private ObjectName getObjectName() {
        String name = traceMonitor.getXOUnit().getName();
        try {
            return new ObjectName("com.buschmais.xo.trace","xo-unit", name);
        } catch (MalformedObjectNameException e) {
            throw new XOException("Cannot create object name for XO unit " + name, e);
        }

    }
}
