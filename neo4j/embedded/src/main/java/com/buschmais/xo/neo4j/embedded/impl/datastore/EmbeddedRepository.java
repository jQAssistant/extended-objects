package com.buschmais.xo.neo4j.embedded.impl.datastore;

import com.buschmais.xo.api.ResultIterable;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedLabel;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedNode;
import com.buschmais.xo.neo4j.spi.AbstractNeo4jRepository;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.spi.session.XOSession;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;

/**
 * Abstract base implementation for Neo4j repositories.
 */
public class EmbeddedRepository extends AbstractNeo4jRepository<EmbeddedLabel> {

    private final GraphDatabaseService graphDatabaseService;

    protected EmbeddedRepository(GraphDatabaseService graphDatabaseService, XOSession<NodeMetadata<EmbeddedLabel>, EmbeddedLabel, ?, ?> xoSession) {
        super(xoSession);
        this.graphDatabaseService = graphDatabaseService;
    }

    @Override
    protected <T> ResultIterable<T> find(EmbeddedLabel label, PropertyMetadata datastoreMetadata, Object datastoreValue) {
        String propertyName = datastoreMetadata.getName();
        ResourceIterator<Node> iterator = graphDatabaseService.findNodes(label.getDelegate(), propertyName, datastoreValue);
        return xoSession.toResult(new ResultIterator<EmbeddedNode>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public EmbeddedNode next() {
                return new EmbeddedNode(iterator.next());
            }

            @Override
            public void close() {
                iterator.close();
            }
        });
    }
}
