package com.buschmais.xo.neo4j.remote.impl.model.state;

import java.util.HashMap;

import com.buschmais.xo.neo4j.remote.impl.model.RemoteLabel;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NodeStateTest {

    private NodeState nodeState;

    @Before
    public void setUp() {
        nodeState = new NodeState();
    }

    @Test
    public void afterCompletionWithClear() {
        // Given
        nodeState.load(new HashMap<>());
        nodeState.getLabels()
            .add(new RemoteLabel("Test"));

        // When
        nodeState.afterCompletion(true);

        // Then
        assertThat(nodeState.isLoaded()).isFalse();
        assertThat(nodeState.getLabels()).isNotNull();
    }

    @Test
    public void afterCompletionWithoutClear() {
        // Given
        nodeState.load(new HashMap<>());
        nodeState.getLabels()
            .add(new RemoteLabel("Test"));

        // When
        nodeState.afterCompletion(false);

        // Then
        assertThat(nodeState.isLoaded()).isTrue();
        assertThat(nodeState.getLabels()).isNotNull();
    }
}
