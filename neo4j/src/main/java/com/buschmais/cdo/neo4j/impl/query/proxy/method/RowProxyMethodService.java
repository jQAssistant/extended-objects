package com.buschmais.cdo.neo4j.impl.query.proxy.method;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.api.Query;
import com.buschmais.cdo.neo4j.impl.common.proxy.method.AbstractProxyMethodService;
import com.buschmais.cdo.neo4j.impl.common.proxy.method.composite.AsMethod;
import com.buschmais.cdo.neo4j.impl.common.reflection.BeanMethod;
import com.buschmais.cdo.neo4j.impl.common.reflection.BeanMethodProvider;
import com.buschmais.cdo.neo4j.impl.common.reflection.BeanPropertyMethod;
import com.buschmais.cdo.neo4j.impl.query.proxy.method.object.EqualsMethod;
import com.buschmais.cdo.neo4j.impl.query.proxy.method.object.HashCodeMethod;
import com.buschmais.cdo.neo4j.impl.query.proxy.method.object.ToStringMethod;
import com.buschmais.cdo.neo4j.impl.query.proxy.method.property.GetMethod;
import com.buschmais.cdo.neo4j.impl.query.proxy.method.row.GetColumnsMethod;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.buschmais.cdo.api.Query.Result.CompositeRowObject;

public class RowProxyMethodService extends AbstractProxyMethodService<Map<String, Object>, RowProxyMethod> {

    public RowProxyMethodService(List<Class<?>> types) {
        BeanMethodProvider beanMethodProvider = new BeanMethodProvider();
        for (Class<?> type : types) {
            Collection<BeanMethod> beanMethodsOfType = beanMethodProvider.getMethods(type);
            for (BeanMethod beanMethod : beanMethodsOfType) {
                if (!(beanMethod instanceof BeanPropertyMethod) || !BeanPropertyMethod.MethodType.GETTER.equals(((BeanPropertyMethod) beanMethod).getMethodType())) {
                    throw new CdoException("Only get methods are supported for projections: '" + beanMethod.getMethod().getName() + "'.");
                }
                BeanPropertyMethod beanPropertyMethod = (BeanPropertyMethod) beanMethod;
                GetMethod proxyMethod = new GetMethod(beanPropertyMethod.getName(), beanPropertyMethod.getType());
                addProxyMethod(proxyMethod, beanPropertyMethod.getMethod());
            }
        }
        addMethod(new AsMethod<Map<String, Object>>(), CompositeObject.class, "as", Class.class);
        addMethod(new com.buschmais.cdo.neo4j.impl.query.proxy.method.row.GetMethod(), CompositeRowObject.class, "get", String.class, Class.class);
        addMethod(new GetColumnsMethod(), CompositeRowObject.class, "getColumns");
        addMethod(new HashCodeMethod(), Object.class, "hashCode");
        addMethod(new EqualsMethod(), Object.class, "equals", Object.class);
        addMethod(new ToStringMethod(), Object.class, "toString");
    }

}
