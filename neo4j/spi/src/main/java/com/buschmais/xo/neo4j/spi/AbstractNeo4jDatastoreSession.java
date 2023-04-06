package com.buschmais.xo.neo4j.spi;

import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.joining;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.api.model.Neo4jLabel;
import com.buschmais.xo.neo4j.api.model.Neo4jNode;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationship;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationshipType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractNeo4jDatastoreSession<N extends Neo4jNode, L extends Neo4jLabel, R extends Neo4jRelationship, T extends Neo4jRelationshipType>
        implements Neo4jDatastoreSession<N, L, R, T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNeo4jDatastoreSession.class);

    public Set<Index> getIndexes() {
        return getIndexes("labelsOrTypes", "properties");
    }

    /**
     * Create the given indexes in the datastore..
     *
     */
    public void createIndexes(Set<Index> indexes) {
        LOGGER.debug("Creating indexes {}.", indexes);
        for (Index index : indexes) {
            String statement = format("CREATE INDEX FOR (n:%s) ON (n.%s)", index.getLabels().stream().collect(joining(":")),
                    index.getProperties().stream().collect(joining(",")));
            try (ResultIterator<Map<String, Object>> iterator = createQuery(Cypher.class).execute(statement, emptyMap())) {
                while (iterator.hasNext()) {
                }
            }
        }
    }

    public String getNeo4jVersion() {
        try (ResultIterator<Map<String, Object>> iterator = createQuery(Cypher.class)
                .execute("CALL dbms.components() YIELD versions UNWIND versions AS version RETURN version", emptyMap())) {
            while (iterator.hasNext()) {
                Map<String, Object> row = iterator.next();
                return (String) row.get("version");
            }
        }
        throw new XOException("Cannot determine Neo4j version.");
    }

    /**
     * Get existing indexes from the datastore.
     *
     * @param labelsColumn
     * @param propertiesColumn
     * @return The existing indexes.
     */
    private Set<Index> getIndexes(String labelsColumn, String propertiesColumn) {
        Set<Index> indexes = new HashSet<>();
        String query = format("SHOW INDEXES YIELD %s AS labels, %s AS properties WHERE labels is not null RETURN labels, properties", labelsColumn, propertiesColumn);
        try (ResultIterator<Map<String, Object>> iterator = createQuery(Cypher.class).execute(query, emptyMap())) {
            while (iterator.hasNext()) {
                Map<String, Object> row = iterator.next();
                List<String> labels = (List<String>) row.get("labels");
                List<String> properties = (List<String>) row.get("properties");
                indexes.add(Index.builder().labels(labels).properties(properties).build());
            }
        }
        return indexes;
    }
}
