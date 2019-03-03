package com.buschmais.xo.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines an method which will execute the given query with the specified
 * parameter values.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ResultOf {

    /**
     * @return The class defining the query.
     */
    Class<?> query() default Object.class;

    /**
     * @return The name of the parameter passed to the query representing the
     *         containing instance ("this").
     */
    String usingThisAs() default "this";

    /**
     * Specifies the parameter binding for a query.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface Parameter {
        /**
         * @return The name of the parameter to be passed to the query.
         */
        String value();
    }

}
