package com.buschmais.xo.impl.proxy.entity;

import java.lang.reflect.Method;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.impl.EntityPropertyManager;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.AbstractProxyMethodService;
import com.buschmais.xo.impl.proxy.common.UnsupportedOperationMethod;
import com.buschmais.xo.impl.proxy.common.composite.GetDelegateMethod;
import com.buschmais.xo.impl.proxy.entity.composite.AsMethod;
import com.buschmais.xo.impl.proxy.entity.composite.GetIdMethod;
import com.buschmais.xo.impl.proxy.entity.object.EqualsMethod;
import com.buschmais.xo.impl.proxy.entity.object.HashCodeMethod;
import com.buschmais.xo.impl.proxy.entity.object.ToStringMethod;
import com.buschmais.xo.impl.proxy.entity.property.*;
import com.buschmais.xo.impl.proxy.entity.resultof.ResultOfMethod;
import com.buschmais.xo.spi.metadata.method.*;
import com.buschmais.xo.spi.metadata.type.TypeMetadata;
import com.buschmais.xo.spi.reflection.AnnotatedMethod;
import com.buschmais.xo.spi.reflection.GetPropertyMethod;
import com.buschmais.xo.spi.reflection.PropertyMethod;
import com.buschmais.xo.spi.reflection.SetPropertyMethod;

public class EntityProxyMethodService<Entity, Relation> extends AbstractProxyMethodService<Entity> {

    public EntityProxyMethodService(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext) {
        for (TypeMetadata typeMetadata : sessionContext.getMetadataProvider().getRegisteredMetadata()) {
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
                        throw new XOException("Cannot instantiate proxy method of type " + proxyMethodType.getName(), e);
                    } catch (IllegalAccessException e) {
                        throw new XOException("Unexpected exception while instantiating type " + proxyMethodType.getName(), e);
                    }
                }
                if (methodMetadata instanceof ResultOfMethodMetadata) {
                    ResultOfMethodMetadata resultOfMethodMetadata = (ResultOfMethodMetadata) methodMetadata;
                    addProxyMethod(new ResultOfMethod(sessionContext, resultOfMethodMetadata), typeMethod.getAnnotatedElement());
                }
                if (methodMetadata instanceof AbstractPropertyMethodMetadata) {
                    PropertyMethod propertyMethod = (PropertyMethod) typeMethod;
                    Method method = propertyMethod.getAnnotatedElement();
                    EntityPropertyManager<Entity, Relation> propertyManager = sessionContext.getEntityPropertyManager();
                    if (methodMetadata instanceof PrimitivePropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            addProxyMethod(new PrimitivePropertyGetMethod(propertyManager, (PrimitivePropertyMethodMetadata) methodMetadata), method);
                        } else if (propertyMethod instanceof SetPropertyMethod) {
                            addProxyMethod(new PrimitivePropertySetMethod(propertyManager, (PrimitivePropertyMethodMetadata) methodMetadata), method);
                        }
                    } else if (methodMetadata instanceof TransientPropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            addProxyMethod(new TransientPropertyGetMethod(propertyManager, (TransientPropertyMethodMetadata) methodMetadata), method);
                        } else if (propertyMethod instanceof SetPropertyMethod) {
                            addProxyMethod(new TransientPropertySetMethod(propertyManager, (TransientPropertyMethodMetadata) methodMetadata), method);
                        }
                    } else if (methodMetadata instanceof EntityReferencePropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            addProxyMethod(new EntityReferencePropertyGetMethod(propertyManager, (EntityReferencePropertyMethodMetadata) methodMetadata),
                                    method);
                        } else if (propertyMethod instanceof SetPropertyMethod) {
                            addProxyMethod(new EntityReferencePropertySetMethod(propertyManager, (EntityReferencePropertyMethodMetadata) methodMetadata),
                                    method);
                        }
                    } else if (methodMetadata instanceof RelationReferencePropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            addProxyMethod(new RelationReferencePropertyGetMethod(propertyManager, (RelationReferencePropertyMethodMetadata) methodMetadata),
                                    method);
                        }
                    } else if (methodMetadata instanceof EntityCollectionPropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            EntityCollectionPropertyGetMethod<Entity, ?> proxyMethod = new EntityCollectionPropertyGetMethod<>(sessionContext,
                                    (EntityCollectionPropertyMethodMetadata<?>) methodMetadata);
                            addProxyMethod(proxyMethod, method);
                        } else if (propertyMethod instanceof SetPropertyMethod) {
                            addProxyMethod(new EntityCollectionPropertySetMethod(propertyManager, (EntityCollectionPropertyMethodMetadata) methodMetadata),
                                    method);
                        }
                    } else if (methodMetadata instanceof RelationCollectionPropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            RelationCollectionPropertyGetMethod<Entity, ?> proxyMethod = new RelationCollectionPropertyGetMethod<>(sessionContext,
                                    (RelationCollectionPropertyMethodMetadata<?>) methodMetadata);
                            addProxyMethod(proxyMethod, method);
                        }
                    }
                }
            }
        }
        addMethod(new AsMethod<>(sessionContext), CompositeObject.class, "as", Class.class);
        addMethod(new GetIdMethod<>(sessionContext), CompositeObject.class, "getId");
        addMethod(new GetDelegateMethod<Entity>(), CompositeObject.class, "getDelegate");
        addMethod(new HashCodeMethod<>(sessionContext), Object.class, "hashCode");
        addMethod(new EqualsMethod<>(sessionContext), Object.class, "equals", Object.class);
        addMethod(new ToStringMethod<>(sessionContext), Object.class, "toString");
    }
}
