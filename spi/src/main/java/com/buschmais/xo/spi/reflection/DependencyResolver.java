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
        Set<T> resolved = new LinkedHashSet<>();
        Set<T> path = new LinkedHashSet<>();

        Deque<Queue<T>> stack = new LinkedList<>();
        stack.push(new LinkedList<>(elements));

        do {
            Queue<T> currentElements = stack.peek();
            if (!currentElements.isEmpty()) {
                T currentElement = currentElements.peek();
                if (!(resolved.contains(currentElement) || path.contains(currentElement))) {
                    path.add(currentElement);
                    Set<T> dependencies = dependencyProvider.getDependencies(currentElement);
                    stack.push(new LinkedList<>(dependencies));
                } else {
                    currentElements.remove();
                }
            } else {
                stack.pop();
                if (!stack.isEmpty()) {
                    T resolvedElement = stack.peek()
                        .remove();
                    path.remove(resolvedElement);
                    resolved.add(resolvedElement);
                }
            }
        } while (!stack.isEmpty());
        return new ArrayList<>(resolved);
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
