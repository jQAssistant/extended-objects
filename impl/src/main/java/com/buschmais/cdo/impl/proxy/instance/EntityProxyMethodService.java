package com.buschmais.cdo.impl.proxy.instance;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.impl.interceptor.InterceptorFactory;
import com.buschmais.cdo.impl.proxy.AbstractProxyMethodService;
import com.buschmais.cdo.impl.proxy.instance.composite.AsMethod;
import com.buschmais.cdo.impl.proxy.instance.object.EqualsMethod;
import com.buschmais.cdo.impl.proxy.instance.object.HashCodeMethod;
import com.buschmais.cdo.impl.proxy.instance.object.ToStringMethod;
import com.buschmais.cdo.impl.proxy.instance.property.*;
import com.buschmais.cdo.impl.proxy.instance.resultof.ResultOfMethod;
import com.buschmais.cdo.spi.datastore.DatastoreSession;
import com.buschmais.cdo.spi.metadata.*;
import com.buschmais.cdo.spi.reflection.BeanMethod;
import com.buschmais.cdo.spi.reflection.GetPropertyMethod;
import com.buschmais.cdo.spi.reflection.PropertyMethod;
import com.buschmais.cdo.spi.reflection.SetPropertyMethod;

import java.lang.reflect.Method;

public class EntityProxyMethodService<Entity, M extends ProxyMethod<?>> extends AbstractProxyMethodService<Entity, M> {

    public EntityProxyMethodService(MetadataProvider metadataProvider, InstanceManager instanceManager, PropertyManager propertyManager,InterceptorFactory interceptorFactory, DatastoreSession datastoreSession) {
        super(instanceManager);
        for (TypeMetadata<?> typeMetadata : metadataProvider.getRegisteredMetadata()) {
            for (AbstractMethodMetadata methodMetadata : typeMetadata.getProperties()) {
                BeanMethod beanMethod = methodMetadata.getBeanMethod();
                if (methodMetadata instanceof UnsupportedOperationMethodMetadata) {
                    addProxyMethod(new UnsupportedOperationMethod((UnsupportedOperationMethodMetadata) methodMetadata), beanMethod.getMethod());
                } else if (methodMetadata instanceof ImplementedByMethodMetadata) {
                    ImplementedByMethodMetadata implementedByMethodMetadata = (ImplementedByMethodMetadata) methodMetadata;
                    Class<? extends ProxyMethod> proxyMethodType = implementedByMethodMetadata.getProxyMethodType();
                    try {
                        addProxyMethod(proxyMethodType.newInstance(), beanMethod.getMethod());
                    } catch (InstantiationException e) {
                        throw new CdoException("Cannot instantiate query method of type " + proxyMethodType.getName(), e);
                    } catch (IllegalAccessException e) {
                        throw new CdoException("Unexpected exception while instantiating type " + proxyMethodType.getName(), e);
                    }
                }
                if (methodMetadata instanceof ResultOfMethodMetadata) {
                    ResultOfMethodMetadata resultOfMethodMetadata = (ResultOfMethodMetadata) methodMetadata;
                    addProxyMethod(new ResultOfMethod(resultOfMethodMetadata, instanceManager, interceptorFactory, datastoreSession), beanMethod.getMethod());
                }
                if (methodMetadata instanceof AbstractPropertyMethodMetadata) {
                    PropertyMethod beanPropertyMethod = (PropertyMethod) beanMethod;
                    Method method = beanPropertyMethod.getMethod();
                    if (methodMetadata instanceof PrimitivePropertyMethodMetadata) {
                        if (beanPropertyMethod instanceof GetPropertyMethod) {
                            addProxyMethod(new PrimitivePropertyGetMethod((PrimitivePropertyMethodMetadata) methodMetadata, instanceManager, propertyManager), method);
                        } else if (beanPropertyMethod instanceof SetPropertyMethod) {
                            addProxyMethod(new PrimitivePropertySetMethod((PrimitivePropertyMethodMetadata) methodMetadata, instanceManager, propertyManager), method);
                        }
                    } else if (methodMetadata instanceof EnumPropertyMethodMetadata) {
                        if (beanPropertyMethod instanceof GetPropertyMethod) {
                            addProxyMethod(new EnumPropertyGetMethod((EnumPropertyMethodMetadata) methodMetadata, instanceManager, propertyManager), method);
                        } else if (beanPropertyMethod instanceof SetPropertyMethod) {
                            addProxyMethod(new EnumPropertySetMethod((EnumPropertyMethodMetadata) methodMetadata, instanceManager, propertyManager), method);
                        }
                    } else if (methodMetadata instanceof ReferencePropertyMethodMetadata) {
                        if (beanPropertyMethod instanceof GetPropertyMethod) {
                            addProxyMethod(new ReferencePropertyGetMethod((ReferencePropertyMethodMetadata) methodMetadata, instanceManager, propertyManager), method);
                        } else if (beanPropertyMethod instanceof SetPropertyMethod) {
                            addProxyMethod(new ReferencePropertySetMethod((ReferencePropertyMethodMetadata) methodMetadata, instanceManager, propertyManager), method);
                        }
                    } else if (methodMetadata instanceof CollectionPropertyMethodMetadata) {
                        if (beanPropertyMethod instanceof GetPropertyMethod) {
                            addProxyMethod(new CollectionPropertyGetMethod((CollectionPropertyMethodMetadata) methodMetadata, instanceManager, propertyManager, interceptorFactory), method);
                        } else if (beanPropertyMethod instanceof SetPropertyMethod) {
                            addProxyMethod(new CollectionPropertySetMethod((CollectionPropertyMethodMetadata) methodMetadata, instanceManager, propertyManager), method);
                        }
                    }
                }
            }
        }
        addMethod(new AsMethod<Entity>(getInstanceManager()), CompositeObject.class, "as", Class.class);
        addMethod(new HashCodeMethod<Entity>(datastoreSession), Object.class, "hashCode");
        addMethod(new EqualsMethod<Entity>(instanceManager, datastoreSession), Object.class, "equals", Object.class);
        addMethod(new ToStringMethod<Entity>(datastoreSession), Object.class, "toString");
    }
}
