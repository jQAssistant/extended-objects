package com.buschmais.xo.impl;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOTransaction;
import com.buschmais.xo.impl.transaction.TransactionalResultIterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TransactionalResultIteratorTest {

    @Mock
    private ResultIterator<String> resultIterator;

    @Mock
    private XOTransaction xoTransaction;

    @Test
    public void transactional() {
        when(xoTransaction.isActive()).thenReturn(true);

        TransactionalResultIterator<String> transactionalResultIterator = new TransactionalResultIterator<>(resultIterator, xoTransaction);

        ArgumentCaptor<XOTransaction.Synchronization> synchronizationCaptor = ArgumentCaptor.forClass(XOTransaction.Synchronization.class);
        verify(xoTransaction).registerSynchronization(synchronizationCaptor.capture());
        verify(xoTransaction, never()).unregisterSynchronization(synchronizationCaptor.getValue());

        transactionalResultIterator.close();

        verify(xoTransaction).unregisterSynchronization(synchronizationCaptor.getValue());
        verify(resultIterator).close();
    }

    @Test
    public void nonTransactional() {
        when(xoTransaction.isActive()).thenReturn(false);

        TransactionalResultIterator<String> transactionalResultIterator = new TransactionalResultIterator<>(resultIterator, xoTransaction);
        transactionalResultIterator.close();

        verify(xoTransaction, never()).registerSynchronization(any(XOTransaction.Synchronization.class));
    }

    @Test
    public void detachResultOnCompletion() {
        when(xoTransaction.isActive()).thenReturn(true);
        TransactionalResultIterator<String> transactionalResultIterator = new TransactionalResultIterator<>(resultIterator, xoTransaction);
        when(resultIterator.hasNext()).thenReturn(true, false);
        when(resultIterator.next()).thenReturn("value");

        transactionalResultIterator.beforeCompletion();
        transactionalResultIterator.afterCompletion(true);

        verify(resultIterator, times(2)).hasNext();
        verify(resultIterator).next();

        assertThat(transactionalResultIterator.hasNext()).isTrue();
        assertThat(transactionalResultIterator.next()).isEqualTo("value");
        assertThat(transactionalResultIterator.hasNext()).isFalse();
    }
}
