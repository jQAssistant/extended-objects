package com.buschmais.xo.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.buschmais.xo.api.proxy.ProxyMethod;

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
