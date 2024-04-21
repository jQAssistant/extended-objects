package com.buschmais.xo.neo4j.spi;

import java.util.Map;

import com.buschmais.xo.api.ResultIterator;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CypherQueryResultIterator implements ResultIterator<Map<String, Object>> {

    @Override
    public final void close() {
        for (Notification notification : dispose()) {
            Notification.Severity severity = notification.getSeverity();
            if (severity.equals(Notification.Severity.WARNING)) {
                log.warn("{} - {}: {} (at {}:{})", notification.getCode(), notification.getTitle(), notification.getDescription(), notification.getLine(),
                    notification.getColumn());
            }
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
    public static class Notification {

        Severity severity;

        String code;

        String title;

        String description;

        int offset;

        int line;

        int column;

        public enum Severity {
            WARNING,
            INFORMATION;
        }
    }

}
