package com.buschmais.xo.neo4j.spi;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;

import static com.buschmais.xo.neo4j.spi.Notification.Severity.WARNING;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CypherQueryResultIteratorTest {

    @Mock
    private Logger logger;

    @Mock
    private Iterator<Map<String, Object>> delegate;

    @Mock
    private Supplier<Iterable<Notification>> notificationSupplier;

    private CypherQueryResultIterator iterator;

    @Before
    public void setUp() {
        iterator = new CypherQueryResultIterator() {

            @Override
            protected Logger getLogger() {
                return logger;
            }

            @Override
            public boolean hasNext() {
                return delegate.hasNext();
            }

            @Override
            public Map<String, Object> next() {
                return delegate.next();
            }

            @Override
            protected Iterable<Notification> dispose() {
                return notificationSupplier.get();
            }
        };
    }

    @Test
    public void dispose() {
        doReturn(List.of(Notification.builder()
            .code("0000")
            .title("A warning")
            .severity(WARNING)
            .description("The description")
            .line(1)
            .column(42)
            .build())).when(notificationSupplier)
            .get();

        iterator.close();

        verify(notificationSupplier).get();
        verify(logger).debug(anyString(), eq(WARNING), eq("0000"), eq("A warning"), eq("The description"), eq(1), eq(42));
    }
}
