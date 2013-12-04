package com.buschmais.cdo.neo4j.impl.common.proxy.method;

import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.api.proxy.ProxyMethod;

import java.lang.reflect.Method;

public class TransactionalProxyMethodService<E, M extends ProxyMethod<?>> implements ProxyMethodService<E, M> {

    private AbstractProxyMethodService<E, M> delegate;

    private CdoUnit.TransactionAttribute transactionAttribute;

    public TransactionalProxyMethodService(AbstractProxyMethodService<E, M> delegate, CdoUnit.TransactionAttribute transactionAttribute) {
        this.delegate = delegate;
        this.transactionAttribute = transactionAttribute;
    }

    @Override
    public Object invoke(E element, Object instance, Method method, Object[] args) {
        return delegate.invoke(element, instance, method, args);
    }
}
