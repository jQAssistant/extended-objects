package com.buschmais.cdo.neo4j.spi;

import java.util.Comparator;
import java.util.TreeSet;

public final class TypeSet extends TreeSet<Class<?>> {
    public TypeSet() {
        super(new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
}
