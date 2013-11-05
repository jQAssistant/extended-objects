package com.buschmais.cdo.neo4j.impl.metadata;

import java.util.*;

public class DependencyResolver<T> {

    private Map<T, Set<T>> blockedBy;
    private Collection<T> elements;
    private DependencyProvider<T> dependencyProvider;

    public DependencyResolver(Collection<T> elements, DependencyProvider<T> dependencyProvider) {
        this.elements = elements;
        this.dependencyProvider = dependencyProvider;
    }

    public List<T> resolve() {
        blockedBy = new HashMap<>();
        for (T dependent : elements) {
            Set<T> dependencies = dependencyProvider.getDependencies(dependent);
            blockedBy.put(dependent, dependencies);
        }
        List<T> result = new LinkedList<>();
        for (T element : elements) {
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
        }
        result.add(element);
    }

    public interface DependencyProvider<T> {
        Set<T> getDependencies(T dependent);
    }
}
