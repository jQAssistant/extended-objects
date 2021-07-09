package com.buschmais.xo.neo4j.remote.impl.model.state;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;

import com.buschmais.xo.neo4j.remote.impl.model.RemoteLabel;

import org.junit.Before;
import org.junit.Test;

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
        nodeState.getLabels().add(new RemoteLabel("Test"));

        // When
        nodeState.afterCompletion(true);

        // Then
        assertThat(nodeState.isLoaded(), equalTo(false));
        assertThat(nodeState.getLabels(), notNullValue());
    }

    @Test
    public void afterCompletionWithoutClear() {
        // Given
        nodeState.load(new HashMap<>());
        nodeState.getLabels().add(new RemoteLabel("Test"));

        // When
        nodeState.afterCompletion(false);

        // Then
        assertThat(nodeState.isLoaded(), equalTo(true));
        assertThat(nodeState.getLabels(), notNullValue());
    }
}
