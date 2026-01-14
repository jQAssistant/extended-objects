package com.buschmais.xo.neo4j.remote.impl.datastore;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.spi.CypherQuery;
import com.buschmais.xo.neo4j.spi.CypherQueryResultIterator;
import com.buschmais.xo.neo4j.spi.Notification;
import com.buschmais.xo.neo4j.spi.helper.Converter;

import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Result;
import org.neo4j.driver.summary.InputPosition;
import org.neo4j.driver.summary.ResultSummary;
import org.slf4j.Logger;

import static java.util.stream.Collectors.toList;

@Slf4j
public class RemoteCypherQuery implements CypherQuery {

    private final StatementExecutor statementExecutor;

    private final Converter parameterConverter;

    private final Converter valueConverter;

    public RemoteCypherQuery(StatementExecutor statementExecutor, Converter parameterConverter, Converter valueConverter) {
        this.statementExecutor = statementExecutor;
        this.parameterConverter = parameterConverter;
        this.valueConverter = valueConverter;
    }

    @Override
    public ResultIterator<Map<String, Object>> execute(Cypher query, Map<String, Object> parameters) {
        return execute(query.value(), parameters);
    }

    @Override
    public ResultIterator<Map<String, Object>> execute(String query, Map<String, Object> parameters) {
        Result result = statementExecutor.execute(query, parameterConverter.<Map<String, Object>>convert(parameters));
        return new CypherQueryResultIterator() {

            @Override
            protected Logger getLogger() {
                return log;
            }

            @Override
            public boolean hasNext() {
                return result.hasNext();
            }

            @Override
            public Map<String, Object> next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                Map<String, Object> row = result.next()
                    .asMap();
                Map<String, Object> result = new LinkedHashMap<>(row.size(), 1);
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    String column = entry.getKey();
                    Object value = entry.getValue();
                    result.put(column, valueConverter.convert(value));
                }
                return result;
            }

            @Override
            public Iterable<Notification> dispose() {
                ResultSummary resultSummary = result.consume();
                return resultSummary.notifications()
                    .stream()
                    .map(n -> {
                        Notification.NotificationBuilder notificationBuilder = Notification.builder()
                            .title(n.title())
                            .description(n.description())
                            .code(n.code())
                            .severity(Notification.Severity.from(n.severity()));
                        InputPosition position = n.position();
                        if (position != null) {
                            notificationBuilder.offset(position.offset())
                                .line(position.line())
                                .column(position.column());
                        }
                        return notificationBuilder.build();
                    })
                    .collect(toList());
            }
        };
    }
}
