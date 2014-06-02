package com.buschmais.xo.impl.proxy.example;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.AbstractProxyMethodService;
import com.buschmais.xo.impl.proxy.example.composite.AsMethod;
import com.buschmais.xo.impl.proxy.example.property.PrimitivePropertySetMethod;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.metadata.method.MethodMetadata;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;
import com.buschmais.xo.spi.reflection.AnnotatedMethod;
import com.buschmais.xo.spi.reflection.SetPropertyMethod;

import java.util.Map;

public class ExampleProxyMethodService<Entity> extends AbstractProxyMethodService<Map<PrimitivePropertyMethodMetadata<?>, Object>> {

    public ExampleProxyMethodService(Class<?> type, SessionContext<?, Entity, ?, ?, ?, ?, ?, ?, ?> sessionContext) {
        EntityTypeMetadata<? extends DatastoreEntityMetadata<?>> entityMetadata = sessionContext.getMetadataProvider().getEntityMetadata(type);
        for (MethodMetadata<?, ?> methodMetadata : entityMetadata.getProperties()) {
            if (methodMetadata instanceof PrimitivePropertyMethodMetadata<?>) {
                AnnotatedMethod method = methodMetadata.getAnnotatedMethod();
                if (method instanceof SetPropertyMethod) {
                    addProxyMethod(new PrimitivePropertySetMethod((PrimitivePropertyMethodMetadata<?>) methodMetadata), method.getAnnotatedElement());
                }
            }
        }
        addMethod(new AsMethod(), CompositeObject.class, "as", Class.class);
    }

}
