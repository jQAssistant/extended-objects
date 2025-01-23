package com.buschmais.xo.impl.proxy.query;

import java.util.Collection;
import java.util.Map;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.metadata.reflection.AnnotatedMethod;
import com.buschmais.xo.api.metadata.reflection.GetPropertyMethod;
import com.buschmais.xo.api.metadata.reflection.PropertyMethod;
import com.buschmais.xo.api.metadata.type.CompositeType;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.AbstractProxyMethodService;
import com.buschmais.xo.impl.proxy.common.composite.GetDelegateMethod;
import com.buschmais.xo.impl.proxy.query.composite.AsMethod;
import com.buschmais.xo.impl.proxy.query.object.EqualsMethod;
import com.buschmais.xo.impl.proxy.query.object.HashCodeMethod;
import com.buschmais.xo.impl.proxy.query.object.ToStringMethod;
import com.buschmais.xo.impl.proxy.query.property.GetMethod;
import com.buschmais.xo.impl.proxy.query.row.GetColumnsMethod;
import com.buschmais.xo.spi.reflection.BeanMethodProvider;

import static com.buschmais.xo.api.Query.Result.CompositeRowObject;

public class RowProxyMethodService<Entity, Relation> extends AbstractProxyMethodService<Map<String, Object>> {

    private final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext;

    private final CompositeType compositeType;

    public RowProxyMethodService(Class<?> type, SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
        CompositeType.Builder builder = CompositeType.builder()
            .type(CompositeRowObject.class);
        if (type != null) {
            // query without type parameter
            builder.type(type);
            BeanMethodProvider beanMethodProvider = BeanMethodProvider.newInstance(type);
            Collection<AnnotatedMethod> typeMethodsOfType = beanMethodProvider.getMethods();
            for (AnnotatedMethod typeMethod : typeMethodsOfType) {
                if (!(typeMethod instanceof GetPropertyMethod)) {
                    throw new XOException("Only get methods are supported for projections: '" + type + "#" + typeMethod.getAnnotatedElement()
                        .getName() + "'.");
                }
                PropertyMethod propertyMethod = (PropertyMethod) typeMethod;
                GetMethod proxyMethod = new GetMethod(propertyMethod.getName(), propertyMethod.getType(), sessionContext);
                addProxyMethod(proxyMethod, propertyMethod.getAnnotatedElement());
            }
        }
        this.compositeType = builder.build();
        init();
    }

    private void init() {
        addMethod(new AsMethod(), CompositeObject.class, "as", Class.class);
        addMethod(new GetDelegateMethod<>(sessionContext), CompositeObject.class, "getDelegate");
        addMethod(new com.buschmais.xo.impl.proxy.query.row.GetMethod(sessionContext), CompositeRowObject.class, "get", String.class, Class.class);
        addMethod(new GetColumnsMethod(), CompositeRowObject.class, "getColumns");
        addMethod(new HashCodeMethod(), Object.class, "hashCode");
        addMethod(new EqualsMethod(), Object.class, "equals", Object.class);
        addMethod(new ToStringMethod(), Object.class, "toString");
    }

    public CompositeType getCompositeType() {
        return compositeType;
    }
}
