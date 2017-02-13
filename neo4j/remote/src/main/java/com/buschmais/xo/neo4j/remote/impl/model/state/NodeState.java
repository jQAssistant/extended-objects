package com.buschmais.xo.neo4j.remote.impl.model.state;

import com.buschmais.xo.neo4j.remote.impl.model.RemoteLabel;

import java.util.Map;
import java.util.Set;

public class NodeState extends AbstractPropertyContainerState {

    private Set<RemoteLabel> labels;

    public NodeState(Set<RemoteLabel> labels, Map<String, Object> readCache) {
        super(readCache);
        this.labels = labels;
    }

    public Set<RemoteLabel> getLabels() {
        return labels;
    }
}
