package com.buschmais.xo.impl;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOTransaction;
import com.buschmais.xo.spi.datastore.DatastoreTransaction;

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
        assertThat(xoTransaction.isActive(), equalTo(true));
        xoTransaction.commit();
        assertThat(xoTransaction.isActive(), equalTo(false));
        verify(datastoreTransaction).begin();
        verify(datastoreTransaction).commit();
        verify(datastoreTransaction, never()).rollback();
    }

    @Test
    public void rollback() {
        XOTransaction xoTransaction = new XOTransactionImpl(datastoreTransaction);
        xoTransaction.begin();
        assertThat(xoTransaction.isActive(), equalTo(true));
        xoTransaction.rollback();
        assertThat(xoTransaction.isActive(), equalTo(false));
        verify(datastoreTransaction).begin();
        verify(datastoreTransaction, never()).commit();
        verify(datastoreTransaction).rollback();
        verify(datastoreTransaction, times(2)).isActive();
    }

    @Test
    public void commitOnRollbackOnly() {
        XOTransaction xoTransaction = new XOTransactionImpl(datastoreTransaction);
        xoTransaction.begin();
        assertThat(xoTransaction.isActive(), equalTo(true));
        xoTransaction.setRollbackOnly();
        try {
            xoTransaction.commit();
            fail("Expecting an " + XOException.class.getName());
        } catch (XOException e) {
            assertThat(e.getMessage(), containsString("rollback only"));
        }
        assertThat(xoTransaction.isActive(), equalTo(true));
        verify(datastoreTransaction).begin();
        verify(datastoreTransaction, never()).commit();
        verify(datastoreTransaction, never()).rollback();
        verify(datastoreTransaction, times(2)).isActive();
    }

    @Test
    public void rollbackOnClose() {
        XOTransactionImpl xoTransaction = new XOTransactionImpl(datastoreTransaction);
        try (XOTransaction tx = xoTransaction.begin()) {
            assertThat(xoTransaction.isActive(), equalTo(true));
            tx.setRollbackOnly();
            assertThat(tx.isRollbackOnly(), equalTo(true));
        }
        assertThat(xoTransaction.isActive(), equalTo(false));
        assertThat(xoTransaction.isRollbackOnly(), equalTo(false));
        verify(datastoreTransaction).begin();
        verify(datastoreTransaction, never()).commit();
        verify(datastoreTransaction).rollback();
        verify(datastoreTransaction, times(2)).isActive();
    }

    @Test
    public void commitOnClose() {
        XOTransactionImpl xoTransaction = new XOTransactionImpl(datastoreTransaction);
        try (XOTransaction tx = xoTransaction.begin()) {
            assertThat(tx.isActive(), equalTo(true));
        }
        assertThat(xoTransaction.isActive(), equalTo(false));
        verify(datastoreTransaction).begin();
        verify(datastoreTransaction).commit();
        verify(datastoreTransaction, never()).rollback();
        verify(datastoreTransaction, times(2)).isActive();
    }
}
