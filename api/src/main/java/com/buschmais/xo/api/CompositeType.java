package com.buschmais.xo.api;

/**
 * Defines a composite type which consists of a number of classes.
 */
public interface CompositeType {

    /**
     * Return the classes which represent this composite type.
     * 
     * @return The classes.
     */
    Class<?>[] getClasses();
}
