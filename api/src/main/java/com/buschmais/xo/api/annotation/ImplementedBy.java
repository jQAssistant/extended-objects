package com.buschmais.xo.api.annotation;

import com.buschmais.xo.api.proxy.ProxyMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adds a custom implementation to any method of a composite object.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImplementedBy {

    /**
     * @return The class containing the custom implementation.
     */
    Class<? extends ProxyMethod<?>> value();

}
