package com.buschmais.xo.impl.proxy.repository;

import java.lang.reflect.Method;

import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.AbstractProxyMethodService;
import com.buschmais.xo.impl.proxy.common.DelegateMethod;
import com.buschmais.xo.impl.proxy.repository.composite.ResultOfMethod;
import com.buschmais.xo.impl.proxy.repository.object.EqualsMethod;
import com.buschmais.xo.impl.proxy.repository.object.HashCodeMethod;
import com.buschmais.xo.impl.proxy.repository.object.ToStringMethod;
import com.buschmais.xo.spi.metadata.method.MethodMetadata;
import com.buschmais.xo.spi.metadata.method.ResultOfMethodMetadata;
import com.buschmais.xo.spi.metadata.type.RepositoryTypeMetadata;
import com.buschmais.xo.spi.metadata.type.TypeMetadata;
import com.buschmais.xo.spi.reflection.AnnotatedMethod;

public class RepositoryProxyMethodService<T, Entity, Relation> extends AbstractProxyMethodService<T> {

    public RepositoryProxyMethodService(T datastoreRepository, RepositoryTypeMetadata repositoryMetadata,
            SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext) {
        for (TypeMetadata typeMetadata : sessionContext.getMetadataProvider().getRegisteredMetadata().values()) {
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
        Class<?> repositoryType = repositoryMetadata.getAnnotatedType().getAnnotatedElement();
        for (Method method : repositoryType.getMethods()) {
            if (method.getDeclaringClass().isAssignableFrom(datastoreRepository.getClass())) {
                DelegateMethod<T> proxyMethod = new DelegateMethod<>(datastoreRepository, method);
                addProxyMethod(proxyMethod, method);
            }
        }
        addMethod(new HashCodeMethod(), Object.class, "hashCode");
        addMethod(new EqualsMethod(), Object.class, "equals", Object.class);
        addMethod(new ToStringMethod(), Object.class, "toString");
    }

}
