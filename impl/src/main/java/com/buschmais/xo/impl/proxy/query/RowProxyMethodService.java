package com.buschmais.xo.impl.proxy.query;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.metadata.reflection.AnnotatedMethod;
import com.buschmais.xo.api.metadata.reflection.GetPropertyMethod;
import com.buschmais.xo.api.metadata.reflection.PropertyMethod;
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

import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.buschmais.xo.api.Query.Result.CompositeRowObject;
import static java.util.Comparator.comparing;


public class RowProxyMethodService extends AbstractProxyMethodService<Map<String, Object>> {

    private final SessionContext<?, ?, ?, ?, ?, ?, ?, ?, ?> sessionContext;

    public RowProxyMethodService(Class<?> type, SessionContext<?, ?, ?, ?, ?, ?, ?, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
        TreeSet<Class<?>> types = new TreeSet<>(comparing(Class::getName));
        types.add(type);
        init(types);
    }

    public RowProxyMethodService(SortedSet<Class<?>> types, SessionContext<?, ?, ?, ?, ?, ?, ?, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
        init(types);
    }

    private void init(SortedSet<Class<?>> types) {
        for (Class<?> type : types) {
            BeanMethodProvider beanMethodProvider = BeanMethodProvider.newInstance(type);
            Collection<AnnotatedMethod> typeMethodsOfType = beanMethodProvider.getMethods();
            for (AnnotatedMethod typeMethod : typeMethodsOfType) {
                if (!(typeMethod instanceof GetPropertyMethod)) {
                    throw new XOException("Only get methods are supported for projections: '" + typeMethod.getAnnotatedElement()
                        .getName() + "'.");
                }
                PropertyMethod propertyMethod = (PropertyMethod) typeMethod;
                GetMethod proxyMethod = new GetMethod(propertyMethod.getName(), propertyMethod.getType(), sessionContext);
                addProxyMethod(proxyMethod, propertyMethod.getAnnotatedElement());
            }
        }
        addMethod(new AsMethod(), CompositeObject.class, "as", Class.class);
        addMethod(new GetDelegateMethod<>(), CompositeObject.class, "getDelegate");
        addMethod(new com.buschmais.xo.impl.proxy.query.row.GetMethod(), CompositeRowObject.class, "get", String.class, Class.class);
        addMethod(new GetColumnsMethod(), CompositeRowObject.class, "getColumns");
        addMethod(new HashCodeMethod(), Object.class, "hashCode");
        addMethod(new EqualsMethod(), Object.class, "equals", Object.class);
        addMethod(new ToStringMethod(), Object.class, "toString");
    }

}
