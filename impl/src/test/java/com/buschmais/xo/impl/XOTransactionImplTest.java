package com.buschmais.xo.impl;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOTransaction;
import com.buschmais.xo.spi.datastore.DatastoreTransaction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class XOTransactionImplTest {

    @Spy
    private DatastoreTransaction datastoreTransaction = new DatastoreTransaction() {
        private boolean active;

        @Override
        public void begin() {
            this.active = true;
        }

        @Override
        public void commit() {
            this.active = false;
        }

        @Override
        public void rollback() {
            this.active = false;
        }

        @Override
        public boolean isActive() {
            return this.active;
        }
    };

    @Test
    public void commit() {
        XOTransaction xoTransaction = new XOTransactionImpl(datastoreTransaction);
        xoTransaction.begin();
        assertThat(xoTransaction.isActive()).isTrue();
        xoTransaction.commit();
        assertThat(xoTransaction.isActive()).isFalse();
        verify(datastoreTransaction).begin();
        verify(datastoreTransaction).commit();
        verify(datastoreTransaction, never()).rollback();
    }

    @Test
    public void rollback() {
        XOTransaction xoTransaction = new XOTransactionImpl(datastoreTransaction);
        xoTransaction.begin();
        assertThat(xoTransaction.isActive()).isTrue();
        xoTransaction.rollback();
        assertThat(xoTransaction.isActive()).isFalse();
        verify(datastoreTransaction).begin();
        verify(datastoreTransaction, never()).commit();
        verify(datastoreTransaction).rollback();
        verify(datastoreTransaction, times(2)).isActive();
    }

    @Test
    public void commitOnRollbackOnly() {
        XOTransaction xoTransaction = new XOTransactionImpl(datastoreTransaction);
        xoTransaction.begin();
        assertThat(xoTransaction.isActive()).isTrue();
        xoTransaction.setRollbackOnly();
        try {
            xoTransaction.commit();
            fail("Expecting an " + XOException.class.getName());
        } catch (XOException e) {
            assertThat(e.getMessage()).contains("rollback only");
        }
        assertThat(xoTransaction.isActive()).isTrue();
        verify(datastoreTransaction).begin();
        verify(datastoreTransaction, never()).commit();
        verify(datastoreTransaction, never()).rollback();
        verify(datastoreTransaction, times(2)).isActive();
    }

    @Test
    public void rollbackOnClose() {
        XOTransactionImpl xoTransaction = new XOTransactionImpl(datastoreTransaction);
        try (XOTransaction tx = xoTransaction.begin()) {
            assertThat(xoTransaction.isActive()).isTrue();
            tx.setRollbackOnly();
            assertThat(tx.isRollbackOnly()).isTrue();
        }
        assertThat(xoTransaction.isActive()).isFalse();
        assertThat(xoTransaction.isRollbackOnly()).isFalse();
        verify(datastoreTransaction).begin();
        verify(datastoreTransaction, never()).commit();
        verify(datastoreTransaction).rollback();
        verify(datastoreTransaction, times(2)).isActive();
    }

    @Test
    public void commitOnClose() {
        XOTransactionImpl xoTransaction = new XOTransactionImpl(datastoreTransaction);
        try (XOTransaction tx = xoTransaction.begin()) {
            assertThat(tx.isActive()).isTrue();
        }
        assertThat(xoTransaction.isActive()).isFalse();
        verify(datastoreTransaction).begin();
        verify(datastoreTransaction).commit();
        verify(datastoreTransaction, never()).rollback();
        verify(datastoreTransaction, times(2)).isActive();
    }
}
