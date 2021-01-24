package com.buschmais.xo.spring;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.XOTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.*;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class XOTransactionManager extends AbstractPlatformTransactionManager implements ResourceTransactionManager, BeanFactoryAware, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(XOTransactionManager.class);

    private XOManagerFactory xoManagerFactory;

    public XOTransactionManager(XOManagerFactory xoManagerFactory) {
        setTransactionSynchronization(SYNCHRONIZATION_ON_ACTUAL_TRANSACTION);
        this.xoManagerFactory = xoManagerFactory;
    }

    public void setXOManagerFactory(XOManagerFactory xoManagerFactory) {
        this.xoManagerFactory = xoManagerFactory;
    }

    public XOManagerFactory getXOManagerFactory() {
        return this.xoManagerFactory;
    }

    /**
     * Retrieves a default SessionFactory bean.
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (getXOManagerFactory() == null) {
            setXOManagerFactory(beanFactory.getBean(XOManagerFactory.class));
        }
    }

    @Override
    public void afterPropertiesSet() {
        if (getXOManagerFactory() == null) {
            throw new IllegalArgumentException("'xoManagerFactory' is required");
        }
    }

    @Override
    public Object getResourceFactory() {
        return getXOManagerFactory();
    }

    @Override
    protected Object doGetTransaction() {
        XOTransactionObject txObject = new XOTransactionObject();
        XOManagerHolder xoManagerHolder = (XOManagerHolder) TransactionSynchronizationManager.getResource(getXOManagerFactory());
        if (xoManagerHolder != null) {
            logger.debug("Found thread-bound XOManager ", xoManagerHolder.getXOManager());
            txObject.setXOManagerHolder(xoManagerHolder, false);
        }
        return txObject;
    }

    @Override
    protected boolean isExistingTransaction(Object transaction) {
        return ((XOTransactionObject) transaction).hasTransaction();
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
        XOTransactionObject txObject = (XOTransactionObject) transaction;

        try {
            if (txObject.getXOManagerHolder() == null || txObject.getXOManagerHolder().isSynchronizedWithTransaction()) {
                XOManager xoManager = xoManagerFactory.createXOManager();
                logger.debug("Opened new XOManager {}.", xoManager);
                txObject.setXOManagerHolder(new XOManagerHolder(xoManager), true);
            }

            XOManager xoManager = txObject.getXOManagerHolder().getXOManager();

            if (definition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT) {
                // We should set a specific isolation level but are not allowed to...
                throw new InvalidIsolationLevelException("XOTransactionManager only supports default transaction isolation.");
            }

            if (definition.getPropagationBehavior() != TransactionDefinition.PROPAGATION_REQUIRED) {
                throw new IllegalTransactionStateException("XOTransactionManager only supports 'required' propagation.");
            }

            XOTransaction xoTransaction = xoManager.currentTransaction().begin();

            txObject.setXOTransaction(xoTransaction);
            logger.debug("Beginning Transaction {} on XOManager {}", xoTransaction, xoManager);

            if (txObject.isNewXOManagerHolder()) {
                TransactionSynchronizationManager.bindResource(getXOManagerFactory(), txObject.getXOManagerHolder());
            }

            if (definition.isReadOnly()) {
                TransactionSynchronizationManager.setCurrentTransactionReadOnly(true);
            }

            txObject.getXOManagerHolder().setSynchronizedWithTransaction(true);
        } catch (TransactionException ex) {
            closeAfterFailedBegin(txObject);
            throw ex;
        } catch (Throwable ex) {
            closeAfterFailedBegin(txObject);
            throw new CannotCreateTransactionException("Could not open XOManager for transaction", ex);
        }
    }

    private void closeAfterFailedBegin(XOTransactionObject txObject) {
        if (txObject.isNewXOManagerHolder()) {
            XOManager xoManager = txObject.getXOManagerHolder().getXOManager();
            try {
                if (xoManager.currentTransaction().isActive()) {
                    xoManager.currentTransaction().rollback();
                }
            } catch (Throwable ex) {
                logger.debug("Could not rollback XOTransaction after failed transaction begin", ex);
            } finally {
                xoManager.close();
                txObject.setXOManagerHolder(null, false);
            }
        }
    }

    @Override
    protected Object doSuspend(Object transaction) {
        XOTransactionObject txObject = (XOTransactionObject) transaction;
        txObject.setXOManagerHolder(null, false);
        XOManagerHolder xoManagerHolder = (XOManagerHolder) TransactionSynchronizationManager.unbindResource(getXOManagerFactory());
        return new SuspendedResourcesHolder(xoManagerHolder);
    }

    @Override
    protected void doResume(Object transaction, Object suspendedResources) {
        SuspendedResourcesHolder resourcesHolder = (SuspendedResourcesHolder) suspendedResources;
        if (TransactionSynchronizationManager.hasResource(getXOManagerFactory())) {
            TransactionSynchronizationManager.unbindResource(getXOManagerFactory());
        }
        TransactionSynchronizationManager.bindResource(getXOManagerFactory(), resourcesHolder.getXoManagerHolder());
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) {
        doComplete(status, true);
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) {
        doComplete(status, false);
    }

    private void doComplete(DefaultTransactionStatus status, boolean commit) {
        XOTransactionObject txObject = (XOTransactionObject) status.getTransaction();
        XOManager xoManager = txObject.getXOManagerHolder().getXOManager();
        XOTransaction xoTransaction = xoManager.currentTransaction();
        try {
            if (xoTransaction.isActive()) {
                if (commit) {
                    logger.debug("Committing XO transaction {} on XOManager {}", xoTransaction, xoManager);
                    xoTransaction.commit();
                } else {
                    logger.debug("Rolling back XO transaction {} on XOManager {}", xoTransaction, xoManager);
                    xoTransaction.rollback();
                }
            }
        } catch (XOException ex) {
            throw new InvalidDataAccessApiUsageException("Cannot complete XOTransaction (commit: " + commit + ")", ex);
        } finally {
            xoManager.close();
        }
    }

    @Override
    protected void doSetRollbackOnly(DefaultTransactionStatus status) {
        XOTransactionObject txObject = (XOTransactionObject) status.getTransaction();
        txObject.getXOTransaction().setRollbackOnly();
        status.setRollbackOnly();
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        XOTransactionObject txObject = (XOTransactionObject) transaction;
        if (txObject.isNewXOManagerHolder()) {
            TransactionSynchronizationManager.unbindResourceIfPossible(getXOManagerFactory());
        }
        XOTransaction xoTransaction = txObject.getXOTransaction();
        if (xoTransaction.isActive()) {
            xoTransaction.rollback();
        }
        if (txObject.isNewXOManagerHolder()) {
            XOManager xoManager = txObject.getXOManagerHolder().getXOManager();
            logger.debug("Closing XOManager {} after transaction.", xoManager);
        } else {
            logger.debug("Not closing pre-bound XOManager after transaction.");
        }

        txObject.getXOManagerHolder().clear();
    }

    private static class XOTransactionObject {

        private XOManagerHolder xoManagerHolder;

        private boolean newXOManagerHolder;

        private XOTransaction xoTransaction;

        void setXOManagerHolder(XOManagerHolder xoManagerHolder, boolean newXOManagerHolder) {
            this.xoManagerHolder = xoManagerHolder;
            this.newXOManagerHolder = newXOManagerHolder;
        }

        XOManagerHolder getXOManagerHolder() {
            return this.xoManagerHolder;
        }

        boolean isNewXOManagerHolder() {
            return this.newXOManagerHolder;
        }

        boolean hasTransaction() {
            return (this.xoManagerHolder != null && this.xoManagerHolder.isTransactionActive());
        }

        void setXOTransaction(XOTransaction xoTransaction) {
            this.xoTransaction = xoTransaction;
        }

        XOTransaction getXOTransaction() {
            return this.xoTransaction;
        }
    }

    private static class SuspendedResourcesHolder {

        private final XOManagerHolder xoManagerHolder;

        private SuspendedResourcesHolder(XOManagerHolder xoManagerHolder) {
            this.xoManagerHolder = xoManagerHolder;
        }

        private XOManagerHolder getXoManagerHolder() {
            return this.xoManagerHolder;
        }
    }
}
