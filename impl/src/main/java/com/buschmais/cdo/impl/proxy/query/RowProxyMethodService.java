package com.buschmais.cdo.impl.proxy.query;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.proxy.AbstractProxyMethodService;
import com.buschmais.cdo.impl.proxy.query.composite.AsMethod;
import com.buschmais.cdo.impl.proxy.query.object.EqualsMethod;
import com.buschmais.cdo.impl.proxy.query.object.HashCodeMethod;
import com.buschmais.cdo.impl.proxy.query.object.ToStringMethod;
import com.buschmais.cdo.impl.proxy.query.property.GetMethod;
import com.buschmais.cdo.impl.proxy.query.row.GetColumnsMethod;
import com.buschmais.cdo.impl.reflection.BeanMethodProvider;
import com.buschmais.cdo.spi.reflection.BeanMethod;
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
            Collection<BeanMethod> beanMethodsOfType = beanMethodProvider.getMethods(type);
            for (BeanMethod beanMethod : beanMethodsOfType) {
                if (!(beanMethod instanceof GetPropertyMethod)) {
                    throw new CdoException("Only get methods are supported for projections: '" + beanMethod.getMethod().getName() + "'.");
                }
                PropertyMethod beanPropertyMethod = (PropertyMethod) beanMethod;
                GetMethod proxyMethod = new GetMethod(beanPropertyMethod.getName(), beanPropertyMethod.getType());
                addProxyMethod(proxyMethod, beanPropertyMethod.getMethod());
            }
        }
        addMethod(new AsMethod<Map<String, Object>>(), CompositeObject.class, "as", Class.class);
        addMethod(new com.buschmais.cdo.impl.proxy.query.row.GetMethod(), CompositeRowObject.class, "get", String.class, Class.class);
        addMethod(new GetColumnsMethod(), CompositeRowObject.class, "getColumns");
        addMethod(new HashCodeMethod(), Object.class, "hashCode");
        addMethod(new EqualsMethod(), Object.class, "equals", Object.class);
        addMethod(new ToStringMethod(), Object.class, "toString");
    }

}
