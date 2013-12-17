package com.buschmais.cdo.spi.datastore;

import java.util.Comparator;
import java.util.TreeSet;

/**
 * Represents a set of types a entity represents.
 */
public final class TypeSet extends TreeSet<Class<?>> {

    /**
     * Constructor.
     */
    public TypeSet() {
        super(new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
}
