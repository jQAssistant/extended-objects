package com.buschmais.xo.spi.metadata;

import java.util.Arrays;
import java.util.Collection;

import com.buschmais.xo.api.CompositeType;

/**
 * A builder for {@link com.buschmais.xo.api.CompositeType}s.
 */
public class CompositeTypeBuilder {

    /**
     * Constructor.
     */
    private CompositeTypeBuilder() {
    }

    public static CompositeType create(Class<?> baseType, Class<?>... types) {
        CompositeTypeImpl compositeType = new CompositeTypeImpl();
        compositeType.classes = new Class<?>[types.length + 1];
        compositeType.classes[0] = baseType;
        arrayCopy(compositeType, types, 1);
        return compositeType;
    }

    public static CompositeType create(Class<?> baseType, Class<?> type, Class<?>[] types) {
        CompositeTypeImpl compositeType = new CompositeTypeImpl();
        compositeType.classes = new Class<?>[types.length + 2];
        compositeType.classes[0] = baseType;
        compositeType.classes[1] = type;
        arrayCopy(compositeType, types, 2);
        return compositeType;
    }

    public static <T> CompositeType create(Class<?> baseType, Collection<T> types, Function<T, Class<?>> typeMapper) {
        CompositeTypeImpl compositeType = new CompositeTypeImpl();
        compositeType.classes = new Class<?>[types.size() + 1];
        compositeType.classes[0] = baseType;
        int i = 1;
        for (T type : types) {
            compositeType.classes[i] = typeMapper.apply(type);
            i++;
        }
        compositeType.hashCode = Arrays.hashCode(compositeType.classes);
        return compositeType;
    }

    private static void arrayCopy(CompositeTypeImpl compositeType, Class<?>[] types, int startIndex) {
        System.arraycopy(types, 0, compositeType.classes, startIndex, types.length);
        compositeType.hashCode = Arrays.hashCode(compositeType.classes);
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
            return Arrays.asList(classes).toString();
        }

    }
}
