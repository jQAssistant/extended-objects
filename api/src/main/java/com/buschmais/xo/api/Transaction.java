package com.buschmais.xo.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the annotation to be used for declarative transaction demarcation.
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Transaction {

    /**
     * The {@link Transaction.TransactionAttribute}.
     *
     * @return The {@link Transaction.TransactionAttribute}.
     */
    TransactionAttribute value();

    /**
     * Defines the supported transaction attributes .
     */
    enum TransactionAttribute {

        /**
         * No transaction management will be performed.
         */
        NONE,
        /**
         * An existing transaction is mandatory, i.e. the application explicitly
         * controls transactions.
         */
        MANDATORY,
        /**
         * An existing transaction will be used, if no transaction exists a new one will
         * be automatically created and committed.
         */
        REQUIRES,
        /**
         * The annotated method does not make use of transactions.
         */
        NOT_SUPPORTED;
    }
}
