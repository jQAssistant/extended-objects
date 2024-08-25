package com.buschmais.xo.spi.reflection;

import java.util.*;

/**
 * Resolvers dependencies between elements.
 *
 * @param <T>
 *     The element type.
 */
public class DependencyResolver<T> {

    private final Collection<T> elements;
    private final DependencyProvider<T> dependencyProvider;

    /**
     * Private constructor.
     *
     * @param elements
     *     The elements to resolver.
     * @param dependencyProvider
     *     The dependency provider.
     */
    private DependencyResolver(Collection<T> elements, DependencyProvider<T> dependencyProvider) {
        this.elements = elements;
        this.dependencyProvider = dependencyProvider;
    }

    /**
     * Creates an instance of the resolver.
     *
     * @param elements
     *     The elements to resolve.
     * @param dependencyProvider
     *     The dependency provider.
     * @param <T>
     *     The element type.
     * @return The resolver.
     */
    public static <T> DependencyResolver<T> newInstance(Collection<T> elements, DependencyProvider<T> dependencyProvider) {
        return new DependencyResolver<>(elements, dependencyProvider);
    }

    /**
     * Resolves the dependencies to a list using a depth-first search.
     *
     * @return The resolved list.
     */
    public List<T> resolve() {
        // the already resolved elements
        Set<T> resolved = new LinkedHashSet<>();
        // the current path in depth-first search, used to detect cycles
        Set<T> path = new LinkedHashSet<>();

        Deque<Queue<T>> stack = new LinkedList<>();
        stack.push(new LinkedList<>(elements));
        do {
            Queue<T> currentElements = stack.peek();
            if (!currentElements.isEmpty()) {
                // if current level provides more elements then evaluate the first one for non-resolved children
                T currentElement = currentElements.peek();
                if (!(resolved.contains(currentElement) || path.contains(currentElement))) {
                    // if current element is not yet resolved then push its children (i.e. dependencies) to the stack to be processed next
                    Set<T> dependencies = dependencyProvider.getDependencies(currentElement);
                    stack.push(new LinkedList<>(dependencies));
                    path.add(currentElement);
                } else {
                    // element is resolved, remove it from current level to evaluate the next one
                    currentElements.remove();
                }
            } else {
                // no more elements in current level, go up to previous level
                stack.pop();
                if (!stack.isEmpty()) {
                    // the current element has been resolved
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
     *     The element type.
     */
    @FunctionalInterface
    public interface DependencyProvider<T> {

        /**
         * Return the dependencies of an element.
         *
         * @param dependent
         *     The element.
         * @return The dependencies.
         */
        Set<T> getDependencies(T dependent);
    }
}
