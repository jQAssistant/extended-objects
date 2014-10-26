package com.buschmais.xo.impl.proxy.example;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.AbstractProxyMethodService;
import com.buschmais.xo.impl.proxy.example.composite.AsMethod;
import com.buschmais.xo.impl.proxy.example.property.PrimitivePropertySetMethod;
import com.buschmais.xo.spi.metadata.method.MethodMetadata;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.TypeMetadata;
import com.buschmais.xo.spi.reflection.AnnotatedMethod;
import com.buschmais.xo.spi.reflection.SetPropertyMethod;

import java.util.Map;

public class ExampleProxyMethodService<Entity> extends AbstractProxyMethodService<Map<PrimitivePropertyMethodMetadata<?>, Object>> {

    public ExampleProxyMethodService(Class<?> type, SessionContext<?, Entity, ?, ?, ?, ?, ?, ?, ?> sessionContext) {
        for (TypeMetadata typeMetadata : sessionContext.getMetadataProvider().getRegisteredMetadata().values()) {
            if (typeMetadata.getAnnotatedType().getAnnotatedElement().isAssignableFrom(type)) {
                for (MethodMetadata<?, ?> methodMetadata : typeMetadata.getProperties()) {
                    if (methodMetadata instanceof PrimitivePropertyMethodMetadata<?>) {
                        AnnotatedMethod method = methodMetadata.getAnnotatedMethod();
                        if (method instanceof SetPropertyMethod) {
                            addProxyMethod(new PrimitivePropertySetMethod((PrimitivePropertyMethodMetadata<?>) methodMetadata), method.getAnnotatedElement());
                        }
                    }
                }
            }
        }
        addMethod(new AsMethod(), CompositeObject.class, "as", Class.class);
    }

}
