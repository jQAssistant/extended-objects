package com.buschmais.cdo.neo4j.api.annotation;

import com.buschmais.cdo.neo4j.api.proxy.NodeProxyMethod;

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
    Class<? extends NodeProxyMethod> value();

}
