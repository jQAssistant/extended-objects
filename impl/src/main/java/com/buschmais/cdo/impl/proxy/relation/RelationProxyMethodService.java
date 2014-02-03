package com.buschmais.cdo.impl.proxy.relation;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.impl.RelationPropertyManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.impl.proxy.AbstractProxyMethodService;
import com.buschmais.cdo.impl.proxy.common.UnsupportedOperationMethod;
import com.buschmais.cdo.impl.proxy.common.composite.AsMethod;
import com.buschmais.cdo.impl.proxy.common.composite.GetDelegateMethod;
import com.buschmais.cdo.impl.proxy.relation.object.EqualsMethod;
import com.buschmais.cdo.impl.proxy.relation.object.HashCodeMethod;
import com.buschmais.cdo.impl.proxy.relation.object.ToStringMethod;
import com.buschmais.cdo.impl.proxy.relation.property.EntityReferencePropertyGetMethod;
import com.buschmais.cdo.impl.proxy.relation.property.PrimitivePropertyGetMethod;
import com.buschmais.cdo.impl.proxy.relation.property.PrimitivePropertySetMethod;
import com.buschmais.cdo.impl.proxy.relation.resultof.ResultOfMethod;
import com.buschmais.cdo.spi.metadata.method.*;
import com.buschmais.cdo.spi.metadata.type.TypeMetadata;
import com.buschmais.cdo.spi.reflection.AnnotatedMethod;
import com.buschmais.cdo.spi.reflection.GetPropertyMethod;
import com.buschmais.cdo.spi.reflection.PropertyMethod;
import com.buschmais.cdo.spi.reflection.SetPropertyMethod;

import java.lang.reflect.Method;

public class RelationProxyMethodService<Entity, Relation, M extends ProxyMethod<?>> extends AbstractProxyMethodService<Relation, M> {

    public RelationProxyMethodService(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext) {
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
                        throw new CdoException("Cannot instantiate proxy method of type " + proxyMethodType.getName(), e);
                    } catch (IllegalAccessException e) {
                        throw new CdoException("Unexpected exception while instantiating type " + proxyMethodType.getName(), e);
                    }
                }
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
                    } else if (methodMetadata instanceof EntityReferencePropertyMethodMetadata) {
                        if (propertyMethod instanceof GetPropertyMethod) {
                            addProxyMethod(new EntityReferencePropertyGetMethod(relationPropertyManager, (EntityReferencePropertyMethodMetadata) methodMetadata), method);
                        }
                    }
                }
            }
        }
        addMethod(new AsMethod<Relation, Entity, Relation>(sessionContext), CompositeObject.class, "as", Class.class);
        addMethod(new GetDelegateMethod<Relation>(), CompositeObject.class, "getDelegate");
        addMethod(new HashCodeMethod<>(sessionContext), Object.class, "hashCode");
        addMethod(new EqualsMethod<>(sessionContext), Object.class, "equals", Object.class);
        addMethod(new ToStringMethod<>(sessionContext), Object.class, "toString");
    }
}
