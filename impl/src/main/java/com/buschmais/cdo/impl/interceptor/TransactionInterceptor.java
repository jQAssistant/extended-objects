package com.buschmais.cdo.impl.interceptor;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoTransaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.buschmais.cdo.api.TransactionAttribute;

public class TransactionInterceptor<T> extends AbstractCdoInterceptor<T> {

    private CdoTransaction cdoTransaction;

    private TransactionAttribute defaultTransactionAttribute;

    public TransactionInterceptor(T delegate, CdoTransaction cdoTransaction, TransactionAttribute defaultTransactionAttribute) {
        super(delegate);
        this.cdoTransaction = cdoTransaction;
        this.defaultTransactionAttribute = defaultTransactionAttribute;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        TransactionAttribute transactionAttribute;
        CdoManager.Transaction transaction = method.getAnnotation(CdoManager.Transaction.class);
        if (transaction != null) {
            transactionAttribute = transaction.value();
        } else {
            transactionAttribute = this.defaultTransactionAttribute;
        }
        switch (transactionAttribute) {
            case MANDATORY:
                if (!this.cdoTransaction.isActive()) {
                    throw new CdoException("An active transaction is MANDATORY when calling method '" + method.getClass().getName() + "#" + method.getName() + "'");
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
            case NOT_SUPPORTED:
                return invoke(method, args);
            default: {
                throw new CdoException("Unsupported transaction attribute '" + transactionAttribute + "'");
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
