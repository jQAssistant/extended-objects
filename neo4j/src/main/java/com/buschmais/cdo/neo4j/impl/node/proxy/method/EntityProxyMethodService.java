package com.buschmais.cdo.neo4j.impl.node.proxy.method;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.impl.proxy.AbstractProxyMethodService;
import com.buschmais.cdo.impl.proxy.instance.composite.AsMethod;
import com.buschmais.cdo.neo4j.api.proxy.NodeProxyMethod;
import com.buschmais.cdo.neo4j.impl.common.InstanceManager;
import com.buschmais.cdo.neo4j.impl.common.PropertyManager;
import com.buschmais.cdo.neo4j.impl.common.reflection.BeanMethod;
import com.buschmais.cdo.neo4j.impl.common.reflection.PropertyMethod;
import com.buschmais.cdo.neo4j.impl.node.metadata.*;
import com.buschmais.cdo.neo4j.impl.node.proxy.method.object.EqualsMethod;
import com.buschmais.cdo.neo4j.impl.node.proxy.method.object.HashCodeMethod;
import com.buschmais.cdo.neo4j.impl.node.proxy.method.object.ToStringMethod;
import com.buschmais.cdo.neo4j.impl.node.proxy.method.property.*;
import com.buschmais.cdo.neo4j.impl.node.proxy.method.resultof.ResultOfMethod;
import com.buschmais.cdo.neo4j.spi.DatastoreSession;
import com.buschmais.cdo.spi.proxy.ProxyMethod;
import org.neo4j.graphdb.Node;

import java.lang.reflect.Method;

public class EntityProxyMethodService extends AbstractProxyMethodService<Node, NodeProxyMethod> {

    public EntityProxyMethodService(MetadataProvider metadataProvider, InstanceManager instanceManager, PropertyManager propertyManager, DatastoreSession datastoreSession) {
        for (EntityMetadata<?> entityMetadata : metadataProvider.getRegisteredNodeMetadata()) {
            for (AbstractMethodMetadata methodMetadata : entityMetadata.getProperties()) {
                BeanMethod beanMethod = methodMetadata.getBeanMethod();
                if (methodMetadata instanceof UnsupportedOperationMethodMetadata) {
                    addProxyMethod(new UnsupportedOperationMethod((UnsupportedOperationMethodMetadata) methodMetadata), beanMethod.getMethod());
                } else if (methodMetadata instanceof ImplementedByMethodMetadata) {
                    ImplementedByMethodMetadata implementedByMethodMetadata = (ImplementedByMethodMetadata) methodMetadata;
                    Class<? extends ProxyMethod> proxyMethodType = implementedByMethodMetadata.getProxyMethodType();
                    try {
                        addProxyMethod(proxyMethodType.newInstance(), beanMethod.getMethod());
                    } catch (InstantiationException e) {
                        throw new CdoException("Cannot instantiate proxy method of type " + proxyMethodType.getName(), e);
                    } catch (IllegalAccessException e) {
                        throw new CdoException("Unexpected exception while instantiating type " + proxyMethodType.getName(), e);
                    }
                }
                if (methodMetadata instanceof ResultOfMethodMetadata) {
                    ResultOfMethodMetadata resultOfMethodMetadata = (ResultOfMethodMetadata) methodMetadata;
                    addProxyMethod(new ResultOfMethod(resultOfMethodMetadata, instanceManager, datastoreSession), beanMethod.getMethod());
                }
                if (methodMetadata instanceof AbstractPropertyMethodMetadata) {
                    PropertyMethod beanPropertyMethod = (PropertyMethod) beanMethod;
                    Method getter = beanPropertyMethod.getGetter();
                    Method setter = beanPropertyMethod.getSetter();
                    if (methodMetadata instanceof PrimitivePropertyMethodMetadata) {
                        if (getter != null) {
                            addProxyMethod(new PrimitivePropertyGetMethod((PrimitivePropertyMethodMetadata) methodMetadata, instanceManager, propertyManager), getter);
                        }
                        if (setter != null) {
                            addProxyMethod(new PrimitivePropertySetMethod((PrimitivePropertyMethodMetadata) methodMetadata, instanceManager, propertyManager), setter);
                        }
                    } else if (methodMetadata instanceof EnumPropertyMethodMetadata) {
                        if (getter != null) {
                            addProxyMethod(new EnumPropertyGetMethod((EnumPropertyMethodMetadata) methodMetadata, instanceManager, propertyManager), getter);
                        }
                        if (setter != null) {
                            addProxyMethod(new EnumPropertySetMethod((EnumPropertyMethodMetadata) methodMetadata, instanceManager, propertyManager), setter);
                        }
                    } else if (methodMetadata instanceof ReferencePropertyMethodMetadata) {
                        if (getter != null) {
                            addProxyMethod(new ReferencePropertyGetMethod((ReferencePropertyMethodMetadata) methodMetadata, instanceManager, propertyManager), getter);
                        }
                        if (setter != null) {
                            addProxyMethod(new ReferencePropertySetMethod((ReferencePropertyMethodMetadata) methodMetadata, instanceManager, propertyManager), setter);
                        }
                    } else if (methodMetadata instanceof CollectionPropertyMethodMetadata) {
                        if (getter != null) {
                            addProxyMethod(new CollectionPropertyGetMethod((CollectionPropertyMethodMetadata) methodMetadata, instanceManager, propertyManager), getter);
                        }
                        if (setter != null) {
                            addProxyMethod(new CollectionPropertySetMethod((CollectionPropertyMethodMetadata) methodMetadata, instanceManager, propertyManager), setter);
                        }
                    }
                }
            }
        }
        addMethod(new AsMethod<Node>(), CompositeObject.class, "as", Class.class);
        addMethod(new HashCodeMethod(), Object.class, "hashCode");
        addMethod(new EqualsMethod(instanceManager), Object.class, "equals", Object.class);
        addMethod(new ToStringMethod(instanceManager), Object.class, "toString");
    }
}
