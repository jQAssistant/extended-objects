package com.buschmais.xo.neo4j.spi;

import java.util.Map;

import com.buschmais.xo.api.ResultIterator;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

@Slf4j
public abstract class CypherQueryResultIterator implements ResultIterator<Map<String, Object>> {

    @Override
    public final void close() {
        for (Notification notification : dispose()) {
            Notification.Severity severity = notification.getSeverity();
            if (severity.equals(Notification.Severity.WARNING)) {
                getLogger().warn("{} - {}: {} (at {}:{})", notification.getCode(), notification.getTitle(), notification.getDescription(),
                    notification.getLine(), notification.getColumn());
            }
        }
    }

    /**
     * Dispose the underlying result and return any created notifications.
     *
     * @return The {@link Notification}s.
     */
    protected abstract Iterable<Notification> dispose();

    protected abstract Logger getLogger();
}
