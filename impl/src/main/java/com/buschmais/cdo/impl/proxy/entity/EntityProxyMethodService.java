package com.buschmais.cdo.impl.proxy.entity;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.MetadataProvider;
import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.impl.ProxyFactory;
import com.buschmais.cdo.impl.interceptor.InterceptorFactory;
import com.buschmais.cdo.impl.proxy.AbstractProxyMethodService;
import com.buschmais.cdo.impl.proxy.common.UnsupportedOperationMethod;
import com.buschmais.cdo.impl.proxy.common.composite.AsMethod;
import com.buschmais.cdo.impl.proxy.entity.object.EqualsMethod;
import com.buschmais.cdo.impl.proxy.entity.object.HashCodeMethod;
import com.buschmais.cdo.impl.proxy.entity.object.ToStringMethod;
import com.buschmais.cdo.impl.proxy.entity.property.*;
import com.buschmais.cdo.impl.proxy.entity.resultof.ResultOfMethod;
import com.buschmais.cdo.spi.datastore.DatastoreSession;
import com.buschmais.cdo.spi.metadata.method.*;
import com.buschmais.cdo.spi.metadata.type.TypeMetadata;
import com.buschmais.cdo.spi.reflection.AnnotatedMethod;
import com.buschmais.cdo.spi.reflection.GetPropertyMethod;
import com.buschmais.cdo.spi.reflection.PropertyMethod;
import com.buschmais.cdo.spi.reflection.SetPropertyMethod;

import java.lang.reflect.Method;

public class EntityProxyMethodService<Entity, M extends ProxyMethod<?>> extends AbstractProxyMethodService<Entity, M> {

    public EntityProxyMethodService(MetadataProvider<?, ?, ?, ?> metadataProvider, InstanceManager<?, Entity, ?, ?, ?, ?> instanceManager, ProxyFactory proxyFactory, PropertyManager<?, Entity, ?, ?> propertyManager, CdoTransaction cdoTransaction, InterceptorFactory interceptorFactory, DatastoreSession<?, Entity, ?, ?, ?, ?, ?, ?> datastoreSession) {
        super(instanceManager, proxyFactory);
        for (TypeMetadata typeMetadata : metadataProvider.getRegisteredMetadata()) {
            for (MethodMetadata methodMetadata : typeMetadata.getProperties()) {
                AnnotatedMethod typeMethod = methodMetadata.getAnnotatedMethod();
                if (methodMetadata instanceof UnsupportedOperationMethodMetadata) {
                    addProxyMethod(new UnsupportedOperationMethod((UnsupportedOperationMethodMetadata) methodMetadata), typeMethod.getAnnotatedElement());
                } else if (methodMetadata instanceof ImplementedByMethodMetadata) {
                    ImplementedByMethodMetadata implementedByMethodMetadata = (ImplementedByMethodMetadata) methodMetadata;
                    Class<? extends ProxyMethod> proxyMethodType = implementedByMethodMetadata.getProxyMethodType();
                    try {
                        addProxyMethod(proxyMethodType.newInstance(), typeMethod.getAnnotatedElement());
                    } catch (InstantiationException e) {
                        throw new CdoException("Cannot instantiate query method of type " + proxyMethodType.getName(), e);
                    } catch (IllegalAccessException e) {
                        throw new CdoException("Unexpected exception while instantiating type " + proxyMethodType.getName(), e);
                    }
                }
                if (methodMetadata instanceof ResultOfMethodMetadata) {
                    ResultOfMethodMetadata resultOfMethodMetadata = (ResultOfMethodMetadata) methodMetadata;
                    addProxyMethod(new ResultOfMethod(resultOfMethodMetadata, instanceManager, proxyFactory, cdoTransaction, interceptorFactory, datastoreSession), typeMethod.getAnnotatedElement());
                }
                if (methodMetadata instanceof AbstractPropertyMethodMetadata) {
                    PropertyMethod propertyMethod = (PropertyMethod) typeMethod;
                    Method method = propertyMethod.getAnnotatedElement();
                    if (methodMetadata instanceof PrimitivePropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            addProxyMethod(new PrimitivePropertyGetMethod((PrimitivePropertyMethodMetadata) methodMetadata, instanceManager, propertyManager), method);
                        } else if (propertyMethod instanceof SetPropertyMethod) {
                            addProxyMethod(new PrimitivePropertySetMethod((PrimitivePropertyMethodMetadata) methodMetadata, instanceManager, propertyManager), method);
                        }
                    } else if (methodMetadata instanceof EnumPropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            addProxyMethod(new EnumPropertyGetMethod((EnumPropertyMethodMetadata) methodMetadata, instanceManager, propertyManager), method);
                        } else if (propertyMethod instanceof SetPropertyMethod) {
                            addProxyMethod(new EnumPropertySetMethod((EnumPropertyMethodMetadata) methodMetadata, instanceManager, propertyManager), method);
                        }
                    } else if (methodMetadata instanceof ReferencePropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            addProxyMethod(new ReferencePropertyGetMethod((ReferencePropertyMethodMetadata) methodMetadata, instanceManager, propertyManager), method);
                        } else if (propertyMethod instanceof SetPropertyMethod) {
                            addProxyMethod(new ReferencePropertySetMethod((ReferencePropertyMethodMetadata) methodMetadata, instanceManager, propertyManager), method);
                        }
                    } else if (methodMetadata instanceof CollectionPropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            CollectionPropertyGetMethod<Entity, ?> proxyMethod = new CollectionPropertyGetMethod<>((CollectionPropertyMethodMetadata<?>) methodMetadata, instanceManager, propertyManager, interceptorFactory);
                            addProxyMethod(proxyMethod, method);
                        } else if (propertyMethod instanceof SetPropertyMethod) {
                            addProxyMethod(new CollectionPropertySetMethod((CollectionPropertyMethodMetadata) methodMetadata, instanceManager, propertyManager), method);
                        }
                    }
                }
            }
        }
        addMethod(new AsMethod<Entity>(getInstanceManager()), CompositeObject.class, "as", Class.class);
        addMethod(new HashCodeMethod<>(datastoreSession), Object.class, "hashCode");
        addMethod(new EqualsMethod<>(instanceManager, datastoreSession), Object.class, "equals", Object.class);
        addMethod(new ToStringMethod<Entity>(datastoreSession), Object.class, "toString");
    }
}
