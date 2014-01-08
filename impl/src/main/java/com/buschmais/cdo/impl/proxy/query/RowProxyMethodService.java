package com.buschmais.cdo.impl.proxy.query;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.proxy.AbstractProxyMethodService;
import com.buschmais.cdo.impl.proxy.common.composite.AsMethod;
import com.buschmais.cdo.impl.proxy.query.object.EqualsMethod;
import com.buschmais.cdo.impl.proxy.query.object.HashCodeMethod;
import com.buschmais.cdo.impl.proxy.query.object.ToStringMethod;
import com.buschmais.cdo.impl.proxy.query.property.GetMethod;
import com.buschmais.cdo.impl.proxy.query.row.GetColumnsMethod;
import com.buschmais.cdo.impl.reflection.BeanMethodProvider;
import com.buschmais.cdo.spi.reflection.AnnotatedMethod;
import com.buschmais.cdo.spi.reflection.GetPropertyMethod;
import com.buschmais.cdo.spi.reflection.PropertyMethod;

import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;

import static com.buschmais.cdo.api.Query.Result.CompositeRowObject;

public class RowProxyMethodService extends AbstractProxyMethodService<Map<String, Object>, RowProxyMethod> {

    public RowProxyMethodService(SortedSet<Class<?>> types, InstanceManager instanceManager) {
        super(instanceManager);
        BeanMethodProvider beanMethodProvider = BeanMethodProvider.newInstance();
        for (Class<?> type : types) {
            Collection<AnnotatedMethod> typeMethodsOfType = beanMethodProvider.getMethods(type);
            for (AnnotatedMethod typeMethod : typeMethodsOfType) {
                if (!(typeMethod instanceof GetPropertyMethod)) {
                    throw new CdoException("Only get methods are supported for projections: '" + typeMethod.getAnnotatedElement().getName() + "'.");
                }
                PropertyMethod propertyMethod = (PropertyMethod) typeMethod;
                GetMethod proxyMethod = new GetMethod(propertyMethod.getName(), propertyMethod.getType());
                addProxyMethod(proxyMethod, propertyMethod.getAnnotatedElement());
            }
        }
        addMethod(new AsMethod<Map<String, Object>>(getInstanceManager()), CompositeObject.class, "as", Class.class);
        addMethod(new com.buschmais.cdo.impl.proxy.query.row.GetMethod(), CompositeRowObject.class, "get", String.class, Class.class);
        addMethod(new GetColumnsMethod(), CompositeRowObject.class, "getColumns");
        addMethod(new HashCodeMethod(), Object.class, "hashCode");
        addMethod(new EqualsMethod(), Object.class, "equals", Object.class);
        addMethod(new ToStringMethod(), Object.class, "toString");
    }

}
