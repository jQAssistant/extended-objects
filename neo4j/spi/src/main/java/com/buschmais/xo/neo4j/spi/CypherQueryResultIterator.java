package com.buschmais.xo.neo4j.spi;

import java.util.Map;

import com.buschmais.xo.api.ResultIterator;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.LoggerFactory;

public abstract class CypherQueryResultIterator implements ResultIterator<Map<String, Object>> {

    @Override
    public final void close() {
        for (Notification notification : dispose()) {
            LoggerFactory.getLogger(CypherQuery.class)
                .info("{} - {} ({}): {} (at {}:{})", notification.getCode(), notification.getTitle(), notification.getSeverity(), notification.getDescription(),
                    notification.getLine(), notification.getColumn());
        }
    }

    /**
     * Dispose the underlying result and return any created notifications.
     *
     * @return The {@link Notification}s.
     */
    protected abstract Iterable<Notification> dispose();

    @Builder
    @Getter
    @ToString
    protected static class Notification {

        String severity;

        String code;

        String title;

        String description;

        int offset;

        int line;

        int column;
    }

}
