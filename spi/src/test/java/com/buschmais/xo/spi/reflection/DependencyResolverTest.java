package com.buschmais.xo.spi.reflection;

import java.util.*;

import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableMap;
import static org.assertj.core.api.Assertions.assertThat;

public class DependencyResolverTest {

    @Test
    public void acyclicGraph() {
        Map<String, Set<String>> elements = GraphBuilder.<String>builder()
            .element("A", "B", "C")
            .element("B", "C")
            .element("C")
            .build();
        List<String> resolved = DependencyResolver.newInstance(elements.keySet(), dependent -> elements.getOrDefault(dependent, emptySet()))
            .resolve();
        assertThat(resolved).containsExactly("C", "B", "A");
    }

    @Test
    public void cyclicGraph() {
        Map<String, Set<String>> elements = GraphBuilder.<String>builder()
            .element("A", "B")
            .element("B", "C")
            .element("C", "A")
            .build();
        List<String> resolved = DependencyResolver.newInstance(elements.keySet(), dependent -> elements.getOrDefault(dependent, emptySet()))
            .resolve();
        assertThat(resolved).containsExactly("C", "B", "A");
    }

    private static class GraphBuilder<T> {
        private Map<T, Set<T>> elements = new LinkedHashMap<>();

        public static <E> GraphBuilder<E> builder() {
            return new GraphBuilder<E>();
        }

        public GraphBuilder<T> element(T element, T... dependencies) {
            elements.put(element, new HashSet<>(asList(dependencies)));
            return this;
        }

        public Map<T, Set<T>> build() {
            return unmodifiableMap(elements);
        }
    }
}
