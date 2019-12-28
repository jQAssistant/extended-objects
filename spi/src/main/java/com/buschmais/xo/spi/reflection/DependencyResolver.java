package com.buschmais.xo.spi.reflection;

import java.util.*;

/**
 * Resolvers dependencies between elements.
 *
 * @param <T>
 *            The element type.
 */
public class DependencyResolver<T> {

    private final Collection<T> elements;
    private final DependencyProvider<T> dependencyProvider;
    private Map<T, Set<T>> blockedBy;

    /**
     * Private constructor.
     *
     * @param elements
     *            The elements to resolver.
     * @param dependencyProvider
     *            The dependency provider.
     */
    private DependencyResolver(Collection<T> elements, DependencyProvider<T> dependencyProvider) {
        this.elements = elements;
        this.dependencyProvider = dependencyProvider;
    }

    /**
     * Creates an instance of the resolver.
     *
     * @param elements
     *            The elements to resolve.
     * @param dependencyProvider
     *            The dependency provider.
     * @param <T>
     *            The element type.
     * @return The resolver.
     */
    public static <T> DependencyResolver<T> newInstance(Collection<T> elements, DependencyProvider<T> dependencyProvider) {
        return new DependencyResolver<>(elements, dependencyProvider);
    }

    /**
     * Resolves the dependencies to a list.
     *
     * @return The resolved list.
     */
    public List<T> resolve() {
        blockedBy = new HashMap<>();
        Set<T> queue = new LinkedHashSet<>();
        Set<T> allElements = new HashSet<>();
        queue.addAll(elements);
        while (!queue.isEmpty()) {
            T element = queue.iterator().next();
            Set<T> dependencies = dependencyProvider.getDependencies(element);
            queue.addAll(dependencies);
            blockedBy.put(element, dependencies);
            queue.remove(element);
            allElements.add(element);
        }
        List<T> result = new LinkedList<>();
        for (T element : allElements) {
            resolve(element, result);
        }
        return result;
    }

    /**
     * Resolves an element.
     *
     * @param element
     *            The element.
     * @param result
     *            The result list.
     */
    private void resolve(T element, List<T> result) {
        Set<T> dependencies = blockedBy.get(element);
        if (dependencies != null) {
            for (T dependency : dependencies) {
                resolve(dependency, result);
            }
            blockedBy.remove(element);
            result.add(element);
        }
    }

    /**
     * Provides dependencies for an element.
     *
     * @param <T>
     *            The element type.
     */
    @FunctionalInterface
    public interface DependencyProvider<T> {

        /**
         * Return the dependencies of an element.
         *
         * @param dependent
         *            The element.
         * @return The dependencies.
         */
        Set<T> getDependencies(T dependent);
    }
}
