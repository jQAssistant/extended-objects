package com.buschmais.xo.spi.metadata;

import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

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
        Map<Class<?>, Class<?>> map = new IdentityHashMap<>(types.length + 1);
        map.put(baseType, null);
        for (Class<?> additionalType : types) {
            map.put(additionalType, null);
        }
        return getCompositeType(map);
    }

    public static CompositeType create(Class<?> baseType, Class<?> type, Class<?>[] types) {
        Map<Class<?>, Class<?>> map = new IdentityHashMap<>(types.length + 2);
        map.put(baseType, null);
        map.put(type, null);
        for (Class<?> additionalType : types) {
            map.put(additionalType, null);
        }
        return getCompositeType(map);
    }

    public static <T> CompositeType create(Class<?> baseType, Collection<T> types, Function<T, Class<?>> typeMapper) {
        Map<Class<?>, Class<?>> map = new IdentityHashMap<>(types.size() + 1);
        map.put(baseType, null);
        for (T type : types) {
            map.put(typeMapper.apply(type), null);
        }
        return getCompositeType(map);
    }

    private static CompositeTypeImpl getCompositeType(Map<Class<?>, Class<?>> map) {
        CompositeTypeImpl compositeType = new CompositeTypeImpl();
        compositeType.classes = map.keySet().toArray(new Class[map.size()]);
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
            return Arrays.asList(classes).toString();
        }

    }
}
