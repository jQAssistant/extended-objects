package com.buschmais.xo.neo4j.impl.datastore;

import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;

import com.buschmais.xo.api.NativeQueryEngine;
import com.buschmais.xo.api.ResultIterator;

public class EmbeddedLuceneQueryEngine implements NativeQueryEngine<LuceneQuery> {

    private final GraphDatabaseService graphDatabaseService;

    public EmbeddedLuceneQueryEngine(final GraphDatabaseService graphDatabaseService) {
        this.graphDatabaseService = graphDatabaseService;
    }

    @Override
    public ResultIterator<Map<String, Object>> execute(final LuceneQuery query, final Map<String, Object> translateParameters) {

        //
        // final NativeQuery nativeQuery = getNativeQuery(expression);
        // nativeQuery.execute(translateParameters(parameters));
        //
        //
        // final boolean b =
        // getGraphDatabaseService().index().getNodeAutoIndexer().isEnabled();
        //
        // final Index<Node> nodeIndex =
        // getGraphDatabaseService().index().forNodes("a");
        // final IndexHits<Node> hits = nodeIndex.query("*");
        // // return new ResourceResultIterator<>(hits.iterator());
        //

        return null;
    }

}
