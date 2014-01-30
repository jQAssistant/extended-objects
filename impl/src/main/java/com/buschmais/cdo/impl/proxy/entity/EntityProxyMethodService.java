package com.buschmais.cdo.impl.proxy.entity;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.impl.proxy.AbstractProxyMethodService;
import com.buschmais.cdo.impl.proxy.common.UnsupportedOperationMethod;
import com.buschmais.cdo.impl.proxy.common.composite.AsMethod;
import com.buschmais.cdo.impl.proxy.common.composite.GetDelegateMethod;
import com.buschmais.cdo.impl.proxy.entity.object.EqualsMethod;
import com.buschmais.cdo.impl.proxy.entity.object.HashCodeMethod;
import com.buschmais.cdo.impl.proxy.entity.object.ToStringMethod;
import com.buschmais.cdo.impl.proxy.entity.property.*;
import com.buschmais.cdo.impl.proxy.entity.resultof.ResultOfMethod;
import com.buschmais.cdo.spi.metadata.method.*;
import com.buschmais.cdo.spi.metadata.type.TypeMetadata;
import com.buschmais.cdo.spi.reflection.AnnotatedMethod;
import com.buschmais.cdo.spi.reflection.GetPropertyMethod;
import com.buschmais.cdo.spi.reflection.PropertyMethod;
import com.buschmais.cdo.spi.reflection.SetPropertyMethod;

import java.lang.reflect.Method;

public class EntityProxyMethodService<Entity, Relation, M extends ProxyMethod<?>> extends AbstractProxyMethodService<Entity, M> {

    public EntityProxyMethodService(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext) {
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
                        throw new CdoException("Cannot instantiate query method of type " + proxyMethodType.getName(), e);
                    } catch (IllegalAccessException e) {
                        throw new CdoException("Unexpected exception while instantiating type " + proxyMethodType.getName(), e);
                    }
                }
                if (methodMetadata instanceof ResultOfMethodMetadata) {
                    ResultOfMethodMetadata resultOfMethodMetadata = (ResultOfMethodMetadata) methodMetadata;
                    addProxyMethod(new ResultOfMethod(sessionContext, resultOfMethodMetadata), typeMethod.getAnnotatedElement());
                }
                if (methodMetadata instanceof AbstractPropertyMethodMetadata) {
                    PropertyMethod propertyMethod = (PropertyMethod) typeMethod;
                    Method method = propertyMethod.getAnnotatedElement();
                    if (methodMetadata instanceof PrimitivePropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            addProxyMethod(new PrimitivePropertyGetMethod(sessionContext, (PrimitivePropertyMethodMetadata) methodMetadata), method);
                        } else if (propertyMethod instanceof SetPropertyMethod) {
                            addProxyMethod(new PrimitivePropertySetMethod(sessionContext, (PrimitivePropertyMethodMetadata) methodMetadata), method);
                        }
                    } else if (methodMetadata instanceof EnumPropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            addProxyMethod(new EnumPropertyGetMethod(sessionContext, (EnumPropertyMethodMetadata) methodMetadata), method);
                        } else if (propertyMethod instanceof SetPropertyMethod) {
                            addProxyMethod(new EnumPropertySetMethod(sessionContext, (EnumPropertyMethodMetadata) methodMetadata), method);
                        }
                    } else if (methodMetadata instanceof EntityReferencePropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            addProxyMethod(new EntityReferencePropertyGetMethod(sessionContext, (EntityReferencePropertyMethodMetadata) methodMetadata), method);
                        } else if (propertyMethod instanceof SetPropertyMethod) {
                            addProxyMethod(new EntityReferencePropertySetMethod(sessionContext, (EntityReferencePropertyMethodMetadata) methodMetadata), method);
                        }
                    } else if (methodMetadata instanceof RelationReferencePropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            addProxyMethod(new RelationReferencePropertyGetMethod(sessionContext, (RelationReferencePropertyMethodMetadata) methodMetadata), method);
                        }
                    } else if (methodMetadata instanceof EntityCollectionPropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            EntityCollectionPropertyGetMethod<Entity, ?> proxyMethod = new EntityCollectionPropertyGetMethod<>(sessionContext, (EntityCollectionPropertyMethodMetadata<?>) methodMetadata);
                            addProxyMethod(proxyMethod, method);
                        } else if (propertyMethod instanceof SetPropertyMethod) {
                            addProxyMethod(new EntityCollectionPropertySetMethod(sessionContext, (EntityCollectionPropertyMethodMetadata) methodMetadata), method);
                        }
                    } else if (methodMetadata instanceof RelationCollectionPropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            RelationCollectionPropertyGetMethod<Entity, ?> proxyMethod = new RelationCollectionPropertyGetMethod<>(sessionContext, (RelationCollectionPropertyMethodMetadata<?>) methodMetadata);
                            addProxyMethod(proxyMethod, method);
                        }
                    }
                }
            }
        }

        addMethod(new AsMethod<Entity, Entity, Relation>(sessionContext), CompositeObject.class, "as", Class.class);
        addMethod(new GetDelegateMethod<Entity>(), CompositeObject.class, "getDelegate");
        addMethod(new HashCodeMethod<>(sessionContext), Object.class, "hashCode");
        addMethod(new EqualsMethod<>(sessionContext), Object.class, "equals", Object.class);
        addMethod(new ToStringMethod<Entity>(sessionContext), Object.class, "toString");
    }
}
