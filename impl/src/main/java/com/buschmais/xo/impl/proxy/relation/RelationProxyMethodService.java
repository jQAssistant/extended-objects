package com.buschmais.xo.impl.proxy.relation;

import java.lang.reflect.Method;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.impl.RelationPropertyManager;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.AbstractProxyMethodService;
import com.buschmais.xo.impl.proxy.common.composite.GetDelegateMethod;
import com.buschmais.xo.impl.proxy.relation.composite.AsMethod;
import com.buschmais.xo.impl.proxy.relation.composite.GetIdMethod;
import com.buschmais.xo.impl.proxy.relation.object.EqualsMethod;
import com.buschmais.xo.impl.proxy.relation.object.HashCodeMethod;
import com.buschmais.xo.impl.proxy.relation.object.ToStringMethod;
import com.buschmais.xo.impl.proxy.relation.property.*;
import com.buschmais.xo.impl.proxy.relation.resultof.ResultOfMethod;
import com.buschmais.xo.spi.metadata.method.*;
import com.buschmais.xo.spi.metadata.type.TypeMetadata;
import com.buschmais.xo.spi.reflection.AnnotatedMethod;
import com.buschmais.xo.spi.reflection.GetPropertyMethod;
import com.buschmais.xo.spi.reflection.PropertyMethod;
import com.buschmais.xo.spi.reflection.SetPropertyMethod;

public class RelationProxyMethodService<Entity, Relation> extends AbstractProxyMethodService<Relation> {

    public RelationProxyMethodService(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext) {
        for (TypeMetadata typeMetadata : sessionContext.getMetadataProvider().getRegisteredMetadata().values()) {
            for (MethodMetadata methodMetadata : typeMetadata.getProperties()) {
                AnnotatedMethod typeMethod = methodMetadata.getAnnotatedMethod();
                addUnsupportedOperationMethod(methodMetadata, typeMethod);
                addImplementedByMethod(methodMetadata, typeMethod);
                if (methodMetadata instanceof ResultOfMethodMetadata) {
                    ResultOfMethodMetadata resultOfMethodMetadata = (ResultOfMethodMetadata) methodMetadata;
                    addProxyMethod(new ResultOfMethod(sessionContext, resultOfMethodMetadata), typeMethod.getAnnotatedElement());
                }
                if (methodMetadata instanceof AbstractPropertyMethodMetadata) {
                    RelationPropertyManager<Entity, Relation> relationPropertyManager = sessionContext.getRelationPropertyManager();
                    PropertyMethod propertyMethod = (PropertyMethod) typeMethod;
                    Method method = propertyMethod.getAnnotatedElement();
                    if (methodMetadata instanceof PrimitivePropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            addProxyMethod(new PrimitivePropertyGetMethod(relationPropertyManager, (PrimitivePropertyMethodMetadata) methodMetadata), method);
                        } else if (propertyMethod instanceof SetPropertyMethod) {
                            addProxyMethod(new PrimitivePropertySetMethod(relationPropertyManager, (PrimitivePropertyMethodMetadata) methodMetadata), method);
                        }
                    } else if (methodMetadata instanceof TransientPropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            addProxyMethod(new TransientPropertyGetMethod(relationPropertyManager, (TransientPropertyMethodMetadata) methodMetadata), method);
                        } else if (propertyMethod instanceof SetPropertyMethod) {
                            addProxyMethod(new TransientPropertySetMethod(relationPropertyManager, (TransientPropertyMethodMetadata) methodMetadata), method);
                        }
                    } else if (methodMetadata instanceof EntityReferencePropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            addProxyMethod(
                                    new EntityReferencePropertyGetMethod(relationPropertyManager, (EntityReferencePropertyMethodMetadata) methodMetadata),
                                    method);
                        }
                    }
                }
            }
        }
        addMethod(new AsMethod<>(sessionContext), CompositeObject.class, "as", Class.class);
        addMethod(new GetIdMethod<Relation>(sessionContext), CompositeObject.class, "getId");
        addMethod(new GetDelegateMethod<Relation>(), CompositeObject.class, "getDelegate");
        addMethod(new HashCodeMethod<>(sessionContext), Object.class, "hashCode");
        addMethod(new EqualsMethod<>(sessionContext), Object.class, "equals", Object.class);
        addMethod(new ToStringMethod<>(sessionContext), Object.class, "toString");
    }
}
