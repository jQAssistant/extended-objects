package com.buschmais.xo.api.metadata.type;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.util.Arrays.asList;

/**
 * Implementation of a {@link CompositeType}.
 */
public class CompositeType {

    private final Class<?>[] classes;

    private final int hashCode;

    /**
     * Constructor.
     */
    CompositeType(Class<?>[] classes) {
        this.classes = classes;
        this.hashCode = Arrays.hashCode(classes);
    }

    public Class<?>[] getClasses() {
        return classes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompositeType)) {
            return false;
        }
        CompositeType that = (CompositeType) o;
        return Arrays.equals(classes, that.classes);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return asList(classes).toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        /**
         * A set containing classes ordered them by assignability: most concrete classes
         * first.
         */
        private final SortedSet<Class<?>> types = new TreeSet<>((type1, type2) -> {
            if (type1.equals(type2)) {
                return 0;
            } else if (type1.isAssignableFrom(type2)) {
                return 1;
            } else if (type2.isAssignableFrom(type1)) {
                return -1;
            }
            return type1.getName()
                .compareTo(type2.getName());
        });

        public Builder type(Class<?> type) {
            types.add(type);
            return this;
        }

        public Builder types(Class<?>[] types) {
            for (Class<?> type : types) {
                this.types.add(type);
            }
            return this;
        }

        public Builder types(Iterable<Class<?>> types) {
            for (Class<?> type : types) {
                this.types.add(type);
            }
            return this;
        }

        public Builder typeMetadata(TypeMetadata typeMetadata) {
            types.add(typeMetadata.getAnnotatedType()
                .getAnnotatedElement());
            return this;
        }

        public CompositeType build() {
            return new CompositeType(types.toArray(new Class<?>[types.size()]));
        }
    }
}
