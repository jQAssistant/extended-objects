package com.buschmais.xo.impl.proxy.entity;

import java.lang.reflect.Method;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.impl.EntityPropertyManager;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.AbstractProxyMethodService;
import com.buschmais.xo.impl.proxy.common.composite.GetDelegateMethod;
import com.buschmais.xo.impl.proxy.entity.composite.AsMethod;
import com.buschmais.xo.impl.proxy.entity.composite.GetIdMethod;
import com.buschmais.xo.impl.proxy.entity.object.EqualsMethod;
import com.buschmais.xo.impl.proxy.entity.object.HashCodeMethod;
import com.buschmais.xo.impl.proxy.entity.object.ToStringMethod;
import com.buschmais.xo.impl.proxy.entity.property.*;
import com.buschmais.xo.impl.proxy.entity.resultof.ResultOfMethod;
import com.buschmais.xo.api.metadata.method.*;
import com.buschmais.xo.api.metadata.type.TypeMetadata;
import com.buschmais.xo.api.metadata.reflection.AnnotatedMethod;
import com.buschmais.xo.api.metadata.reflection.GetPropertyMethod;
import com.buschmais.xo.api.metadata.reflection.PropertyMethod;
import com.buschmais.xo.api.metadata.reflection.SetPropertyMethod;

public class EntityProxyMethodService<Entity, Relation> extends AbstractProxyMethodService<Entity> {

    public EntityProxyMethodService(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext) {
        for (TypeMetadata typeMetadata : sessionContext.getMetadataProvider()
            .getRegisteredMetadata()
            .values()) {
            for (MethodMetadata<?, ?> methodMetadata : typeMetadata.getProperties()) {
                AnnotatedMethod typeMethod = methodMetadata.getAnnotatedMethod();
                addUnsupportedOperationMethod(methodMetadata, typeMethod);
                addImplementedByMethod(methodMetadata, typeMethod);
                if (methodMetadata instanceof ResultOfMethodMetadata) {
                    ResultOfMethodMetadata<?> resultOfMethodMetadata = (ResultOfMethodMetadata) methodMetadata;
                    addProxyMethod(new ResultOfMethod<>(sessionContext, resultOfMethodMetadata), typeMethod.getAnnotatedElement());
                }
                if (methodMetadata instanceof AbstractPropertyMethodMetadata) {
                    PropertyMethod propertyMethod = (PropertyMethod) typeMethod;
                    Method method = propertyMethod.getAnnotatedElement();
                    EntityPropertyManager<Entity, Relation, ?> propertyManager = sessionContext.getEntityPropertyManager();
                    if (methodMetadata instanceof PrimitivePropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            addProxyMethod(new PrimitivePropertyGetMethod<>(propertyManager, (PrimitivePropertyMethodMetadata) methodMetadata), method);
                        } else if (propertyMethod instanceof SetPropertyMethod) {
                            addProxyMethod(new PrimitivePropertySetMethod<>(propertyManager, (PrimitivePropertyMethodMetadata) methodMetadata), method);
                        }
                    } else if (methodMetadata instanceof TransientPropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            addProxyMethod(new TransientPropertyGetMethod<>(propertyManager, (TransientPropertyMethodMetadata) methodMetadata), method);
                        } else if (propertyMethod instanceof SetPropertyMethod) {
                            addProxyMethod(new TransientPropertySetMethod<>(propertyManager, (TransientPropertyMethodMetadata) methodMetadata), method);
                        }
                    } else if (methodMetadata instanceof EntityReferencePropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            addProxyMethod(new EntityReferencePropertyGetMethod<>(propertyManager, (EntityReferencePropertyMethodMetadata) methodMetadata),
                                method);
                        } else if (propertyMethod instanceof SetPropertyMethod) {
                            addProxyMethod(new EntityReferencePropertySetMethod<>(propertyManager, (EntityReferencePropertyMethodMetadata) methodMetadata),
                                method);
                        }
                    } else if (methodMetadata instanceof RelationReferencePropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            addProxyMethod(new RelationReferencePropertyGetMethod<>(propertyManager, (RelationReferencePropertyMethodMetadata) methodMetadata),
                                method);
                        }
                    } else if (methodMetadata instanceof EntityCollectionPropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            EntityCollectionPropertyGetMethod<Entity, ?> proxyMethod = new EntityCollectionPropertyGetMethod<>(sessionContext,
                                (EntityCollectionPropertyMethodMetadata<?>) methodMetadata);
                            addProxyMethod(proxyMethod, method);
                        } else if (propertyMethod instanceof SetPropertyMethod) {
                            addProxyMethod(new EntityCollectionPropertySetMethod<>(propertyManager, (EntityCollectionPropertyMethodMetadata) methodMetadata),
                                method);
                        }
                    } else if (methodMetadata instanceof RelationCollectionPropertyMethodMetadata && propertyMethod instanceof GetPropertyMethod) {
                        RelationCollectionPropertyGetMethod<Entity, ?> proxyMethod = new RelationCollectionPropertyGetMethod<>(sessionContext,
                            (RelationCollectionPropertyMethodMetadata<?>) methodMetadata);
                        addProxyMethod(proxyMethod, method);
                    }
                }
            }
        }
        addMethod(new AsMethod<>(sessionContext), CompositeObject.class, "as", Class.class);
        addMethod(new GetIdMethod<>(sessionContext), CompositeObject.class, "getId");
        addMethod(new GetDelegateMethod<>(), CompositeObject.class, "getDelegate");
        addMethod(new HashCodeMethod<>(), Object.class, "hashCode");
        addMethod(new EqualsMethod<>(sessionContext), Object.class, "equals", Object.class);
        addMethod(new ToStringMethod<>(sessionContext), Object.class, "toString");
    }
}
