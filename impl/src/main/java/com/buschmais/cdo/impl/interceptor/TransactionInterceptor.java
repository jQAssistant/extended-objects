package com.buschmais.cdo.impl.interceptor;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.api.Transaction;

import java.lang.reflect.Method;

public class TransactionInterceptor implements CdoInterceptor {

    private final CdoTransaction cdoTransaction;
    private final Transaction.TransactionAttribute defaultTransactionAttribute;

    public TransactionInterceptor(CdoTransaction cdoTransaction, Transaction.TransactionAttribute defaultTransactionAttribute) {
        this.cdoTransaction = cdoTransaction;
        this.defaultTransactionAttribute = defaultTransactionAttribute;
    }

    @Override
    public Object invoke(InvocationContext context) throws Throwable {
        Method method = context.getMethod();
        Transaction.TransactionAttribute transactionAttribute;
        Transaction transaction = method.getAnnotation(Transaction.class);
        if (transaction != null) {
            transactionAttribute = transaction.value();
        } else {
            transactionAttribute = this.defaultTransactionAttribute;
        }
        switch (transactionAttribute) {
            case MANDATORY:
                if (!this.cdoTransaction.isActive()) {
                    throw new CdoException("An active transaction is MANDATORY when calling method '" +
                            method.getDeclaringClass().getName() + "#" + method.getName() + "'");
                }
                return context.proceed();
            case REQUIRES: {
                if (!this.cdoTransaction.isActive()) {
                    try {
                        this.cdoTransaction.begin();
                        Object result = context.proceed();
                        this.cdoTransaction.commit();
                        return result;
                    } catch (RuntimeException e) {
                        if (this.cdoTransaction.isActive()) {
                            this.cdoTransaction.rollback();
                        }
                        throw e;
                    } catch (Exception e) {
                        if (this.cdoTransaction.isActive()) {
                            this.cdoTransaction.commit();
                        }
                        throw e;
                    }
                } else {
                    return context.proceed();
                }
            }
            case NOT_SUPPORTED:
                return context.proceed();
            default: {
                throw new CdoException("Unsupported transaction attribute '" + transactionAttribute + "'");
            }
        }
    }
}
