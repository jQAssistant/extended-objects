package com.buschmais.xo.spi.metadata;

import static java.util.Arrays.asList;

import java.util.*;

import com.buschmais.xo.api.CompositeType;

/**
 * A builder for {@link com.buschmais.xo.api.CompositeType}s.
 */
public class CompositeTypeBuilder {

    /**
     * A comparator for classes ordering them by assignability - most concrete
     * classes first.
     */
    private static final class ClassComparator implements Comparator<Class<?>> {

        public int compare(Class<?> type1, Class<?> type2) {
            if (type1.equals(type2)) {
                return 0;
            } else if (type1.isAssignableFrom(type2)) {
                return 1;
            } else if (type2.isAssignableFrom(type1)) {
                return -1;
            }
            return type1.getName().compareTo(type2.getName());
        }
    }

    private static final ClassComparator COMPARATOR = new ClassComparator();

    /**
     * Constructor.
     */
    private CompositeTypeBuilder() {
    }

    public static CompositeType create(Class<?> baseType, Class<?>... types) {
        SortedSet<Class<?>> classes = new TreeSet<>(COMPARATOR);
        classes.add(baseType);
        addTypes(classes, types);
        return getCompositeType(classes);
    }

    public static CompositeType create(Class<?> baseType, Class<?> type, Class<?>[] types) {
        SortedSet<Class<?>> classes = new TreeSet<>(COMPARATOR);
        classes.add(baseType);
        classes.add(type);
        addTypes(classes, types);
        return getCompositeType(classes);
    }

    public static <T> CompositeType create(Class<?> baseType, Collection<T> types, Function<T, Class<?>> typeMapper) {
        SortedSet<Class<?>> classes = new TreeSet<>(COMPARATOR);
        classes.add(baseType);
        for (T type : types) {
            classes.add(typeMapper.apply(type));
        }
        return getCompositeType(classes);
    }

    private static void addTypes(SortedSet<Class<?>> classes, Class<?>[] types) {
        classes.addAll(asList(types));
    }

    private static CompositeTypeImpl getCompositeType(Set<Class<?>> classes) {
        CompositeTypeImpl compositeType = new CompositeTypeImpl();
        compositeType.classes = classes.toArray(new Class[classes.size()]);
        compositeType.hashCode = Arrays.hashCode(compositeType.classes);
        return compositeType;
    }

    /**
     * A function to map a input value to an output value.
     *
     * @param <T>
     *            The input type.
     * @param <R>
     *            The output type.
     */
    @FunctionalInterface
    public interface Function<T, R> {

        /**
         * Apply the function.
         *
         * @param t
         *            The input value.
         * @return The output value.
         */
        R apply(T t);

    }

    /**
     * Implementation of a {@link com.buschmais.xo.api.CompositeType}.
     */
    private static class CompositeTypeImpl implements CompositeType {

        private Class<?>[] classes;

        private int hashCode;

        /**
         * Constructor.
         */
        private CompositeTypeImpl() {
        }

        @Override
        public Class<?>[] getClasses() {
            return classes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof CompositeTypeImpl)) {
                return false;
            }
            CompositeTypeImpl that = (CompositeTypeImpl) o;
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

    }
}
