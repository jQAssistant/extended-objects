package com.buschmais.xo.neo4j.test.query;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.api.Neo4jDatastoreSession;
import com.buschmais.xo.neo4j.impl.datastore.Neo4jDatastore;
import com.buschmais.xo.spi.datastore.Datastore;
import com.buschmais.xo.spi.datastore.DatastoreQuery;
import com.buschmais.xo.spi.datastore.DatastoreSession;
import com.buschmais.xo.spi.plugin.QueryLanguagePlugin;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of {@link com.buschmais.xo.spi.plugin.QueryLanguagePlugin}.
 */
public class CustomQueryLanguagePlugin implements QueryLanguagePlugin<CustomQueryLanguagePlugin.CustomQueryLanguage> {

    private static final Pattern QUERY_PATTERN = Pattern.compile("(.*):(.*)=(.*)");

    private Neo4jDatastore<?> datastore;

    @Override
    public Class<CustomQueryLanguage> init(Datastore<?, ?, ?, ?, ?> datastore) {
        if (datastore instanceof Neo4jDatastore) {
            this.datastore = (Neo4jDatastore<?>) datastore;
            return CustomQueryLanguage.class;
        }
        throw new XOException("Datastore not supported");
    }

    @Override
    public DatastoreQuery<CustomQueryLanguage> createQuery(final DatastoreSession<?, ?, ?, ?, ?, ?, ?, ?> session) {
        return new DatastoreQuery<CustomQueryLanguage>() {
            @Override
            public ResultIterator<Map<String, Object>> execute(String query, Map<String, Object> parameters) {
                Matcher matcher = QUERY_PATTERN.matcher(query);
                if (matcher.matches()) {
                    final String label = matcher.group(1);
                    String key = matcher.group(2);
                    String value = matcher.group(3);
                    final ResourceIterator<Node> iterator = ((Neo4jDatastoreSession<?>) session).getGraphDatabaseService()
                            .findNodesByLabelAndProperty(DynamicLabel.label(label), key, value).iterator();
                    return new ResultIterator<Map<String, Object>>() {
                        @Override
                        public void close() {
                            iterator.close();
                        }

                        @Override
                        public boolean hasNext() {
                            return iterator.hasNext();
                        }

                        @Override
                        public Map<String, Object> next() {
                            Map<String, Object> result = new HashMap<>();
                            result.put(label, iterator.next());
                            return result;
                        }

                        @Override
                        public void remove() {
                        }
                    };
                }
                throw new XOException("Cannot parse query.");
            }

            @Override
            public ResultIterator<Map<String, Object>> execute(CustomQueryLanguage query, Map<String, Object> parameters) {
                return execute(query.value(), parameters);
            }
        };
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface CustomQueryLanguage {
        String value();
    }
}
