package com.buschmais.xo.impl.proxy.repository;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.AbstractProxyMethodService;
import com.buschmais.xo.impl.proxy.common.UnsupportedOperationMethod;
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
            }
        }
    }

}
