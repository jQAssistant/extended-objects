package com.buschmais.xo.impl.interceptor;

import com.buschmais.xo.api.Transaction;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOTransaction;

import java.lang.reflect.Method;

public class TransactionInterceptor implements CdoInterceptor {

    private final XOTransaction XOTransaction;
    private final Transaction.TransactionAttribute defaultTransactionAttribute;

    public TransactionInterceptor(XOTransaction XOTransaction, Transaction.TransactionAttribute defaultTransactionAttribute) {
        this.XOTransaction = XOTransaction;
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
                if (!this.XOTransaction.isActive()) {
                    throw new XOException("An active transaction is MANDATORY when calling method '" +
                            method.getDeclaringClass().getName() + "#" + method.getName() + "'");
                }
                return context.proceed();
            case REQUIRES: {
                if (!this.XOTransaction.isActive()) {
                    try {
                        this.XOTransaction.begin();
                        Object result = context.proceed();
                        this.XOTransaction.commit();
                        return result;
                    } catch (RuntimeException e) {
                        if (this.XOTransaction.isActive()) {
                            this.XOTransaction.rollback();
                        }
                        throw e;
                    } catch (Exception e) {
                        if (this.XOTransaction.isActive()) {
                            this.XOTransaction.commit();
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
