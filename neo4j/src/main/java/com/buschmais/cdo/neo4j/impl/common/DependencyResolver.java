package com.buschmais.cdo.neo4j.impl.common;

import java.util.*;

public class DependencyResolver<T> {

    private Map<T, Set<T>> blockedBy;
    private Collection<T> elements;
    private DependencyProvider<T> dependencyProvider;

    private DependencyResolver(Collection<T> elements, DependencyProvider<T> dependencyProvider) {
        this.elements = elements;
        this.dependencyProvider = dependencyProvider;
    }

    public static <T> DependencyResolver<T> newInstance(Collection<T> elements, DependencyProvider<T> dependencyProvider) {
        return new DependencyResolver<T>(elements, dependencyProvider);
    }

    public List<T> resolve() {
        blockedBy = new HashMap<>();
        LinkedHashSet<T> queue = new LinkedHashSet<>();
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

    public interface DependencyProvider<T> {
        Set<T> getDependencies(T dependent);
    }
}
