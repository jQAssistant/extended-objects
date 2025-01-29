package com.buschmais.xo.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ResultIterableTest {

    @Mock
    private ResultIterator<String> resultIterator;

    @Test
    public void asStream() {

        ResultIterable<String> resultIterable = new ResultIterable<>() {
            @Override
            public String getSingleResult() {
                return null;
            }

            @Override
            public boolean hasResult() {
                return false;
            }

            @Override
            public ResultIterator<String> iterator() {
                return resultIterator;
            }
        };

        resultIterable.asStream()
            .close();

        verify(resultIterator).close();
    }
}
