package com.buschmais.xo.impl.proxy.repository;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.AbstractProxyMethodService;
import com.buschmais.xo.impl.proxy.common.UnsupportedOperationMethod;
import com.buschmais.xo.impl.proxy.repository.object.EqualsMethod;
import com.buschmais.xo.impl.proxy.repository.object.HashCodeMethod;
import com.buschmais.xo.impl.proxy.repository.object.ToStringMethod;
import com.buschmais.xo.impl.proxy.repository.composite.ResultOfMethod;
import com.buschmais.xo.spi.metadata.method.ImplementedByMethodMetadata;
import com.buschmais.xo.spi.metadata.method.MethodMetadata;
import com.buschmais.xo.spi.metadata.method.ResultOfMethodMetadata;
import com.buschmais.xo.spi.metadata.method.UnsupportedOperationMethodMetadata;
import com.buschmais.xo.spi.metadata.type.TypeMetadata;
import com.buschmais.xo.spi.reflection.AnnotatedMethod;

public class RepositoryProxyMethodService<Entity, Relation> extends AbstractProxyMethodService<XOManager> {

    public RepositoryProxyMethodService(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext) {
        for (TypeMetadata typeMetadata : sessionContext.getMetadataProvider().getRegisteredMetadata()) {
            for (MethodMetadata methodMetadata : typeMetadata.getProperties()) {
                AnnotatedMethod typeMethod = methodMetadata.getAnnotatedMethod();
                addUnsupportedOperationMethod(methodMetadata, typeMethod);
                addImplementedByMethod(methodMetadata, typeMethod);
                if (methodMetadata instanceof ResultOfMethodMetadata) {
                    ResultOfMethodMetadata resultOfMethodMetadata = (ResultOfMethodMetadata) methodMetadata;
                    addProxyMethod(new ResultOfMethod(sessionContext, resultOfMethodMetadata), typeMethod.getAnnotatedElement());
                }
            }
        }
        addMethod(new HashCodeMethod(), Object.class, "hashCode");
        addMethod(new EqualsMethod(), Object.class, "equals", Object.class);
        addMethod(new ToStringMethod(), Object.class, "toString");
    }

}
