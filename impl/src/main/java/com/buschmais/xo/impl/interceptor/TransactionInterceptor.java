package com.buschmais.xo.impl.interceptor;

import com.buschmais.xo.api.Transaction;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOTransaction;
import com.buschmais.xo.spi.interceptor.InvocationContext;
import com.buschmais.xo.spi.interceptor.XOInterceptor;

import java.lang.reflect.Method;

public class TransactionInterceptor implements XOInterceptor {

    private final XOTransaction xoTransaction;
    private final Transaction.TransactionAttribute defaultTransactionAttribute;

    public TransactionInterceptor(XOTransaction xoTransaction, Transaction.TransactionAttribute defaultTransactionAttribute) {
        this.xoTransaction = xoTransaction;
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
                if (!this.xoTransaction.isActive()) {
                    throw new XOException("An active transaction is MANDATORY when calling method '" +
                            method.getDeclaringClass().getName() + "#" + method.getName() + "'");
                }
                return context.proceed();
            case REQUIRES: {
                if (!this.xoTransaction.isActive()) {
                    try {
                        this.xoTransaction.begin();
                        Object result = context.proceed();
                        this.xoTransaction.commit();
                        return result;
                    } catch (RuntimeException e) {
                        if (this.xoTransaction.isActive()) {
                            this.xoTransaction.rollback();
                        }
                        throw e;
                    } catch (Exception e) {
                        if (this.xoTransaction.isActive()) {
                            this.xoTransaction.commit();
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
                throw new XOException("Unsupported transaction attribute '" + transactionAttribute + "'");
            }
        }
    }
}
