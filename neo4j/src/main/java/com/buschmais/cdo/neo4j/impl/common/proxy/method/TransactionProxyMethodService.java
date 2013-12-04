package com.buschmais.cdo.neo4j.impl.common.proxy.method;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.api.proxy.ProxyMethod;

import java.lang.reflect.Method;

public class TransactionProxyMethodService<E, M extends ProxyMethod<?>> implements ProxyMethodService<E, M> {

    private ProxyMethodService<E, M> delegate;

    private CdoTransaction cdoTransaction;

    private CdoUnit.TransactionAttribute transactionAttribute;

    public TransactionProxyMethodService(ProxyMethodService<E, M> delegate, CdoTransaction cdoTransaction, CdoUnit.TransactionAttribute transactionAttribute) {
        this.delegate = delegate;
        this.cdoTransaction = cdoTransaction;
        this.transactionAttribute = transactionAttribute;
    }

    @Override
    public Object invoke(E element, Object instance, Method method, Object[] args) throws Exception {
        switch (transactionAttribute) {
            case MANDATORY:
                if (!this.cdoTransaction.isActive()) {
                    throw new CdoException("An active transaction is MANDATORY when calling method '" + method.getName());
                }
                return delegate.invoke(element, instance, method, args);
            case REQUIRES: {
                if (!this.cdoTransaction.isActive()) {
                    try {
                        this.cdoTransaction.begin();
                        Object result = delegate.invoke(element, instance, method, args);
                        this.cdoTransaction.commit();
                        return result;
                    } catch (RuntimeException e) {
                        this.cdoTransaction.rollback();
                        throw e;
                    } catch (Exception e) {
                        this.cdoTransaction.commit();
                        throw e;
                    }
                }
                return delegate.invoke(element, instance, method, args);
            }
            default:
                throw new CdoException("Unsupported transaction attribute " + transactionAttribute);
        }
    }
}
