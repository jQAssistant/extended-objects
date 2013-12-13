package com.buschmais.cdo.impl.interceptor;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoTransaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.buschmais.cdo.api.CdoManagerFactory.TransactionAttribute;

public class TransactionInterceptor<T> extends AbstractCdoInterceptor<T> {

    private CdoTransaction cdoTransaction;

    private TransactionAttribute transactionAttribute;

    public TransactionInterceptor(T delegate, CdoTransaction cdoTransaction, TransactionAttribute transactionAttribute) {
        super(delegate);
        this.cdoTransaction = cdoTransaction;
        this.transactionAttribute = transactionAttribute;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch (transactionAttribute) {
            case MANDATORY:
                if (!this.cdoTransaction.isActive()) {
                    throw new CdoException("An active transaction is MANDATORY when calling method '" + method.getName());
                }
                return invoke(method, args);
            case REQUIRES: {
                if (!this.cdoTransaction.isActive()) {
                    try {
                        this.cdoTransaction.begin();
                        Object result = invoke(method, args);
                        this.cdoTransaction.commit();
                        return result;
                    } catch (RuntimeException e) {
                        this.cdoTransaction.rollback();
                        throw e;
                    } catch (Exception e) {
                        this.cdoTransaction.commit();
                        throw e;
                    }
                } else {
                    return invoke(method, args);
                }
            }
            default: {
                throw new CdoException("Unsupported transaction attribute " + transactionAttribute);
            }
        }
    }

    private Object invoke(Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(getDelegate(), args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}
