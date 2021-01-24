package com.buschmais.xo.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.XOManagerFactory;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ XOManagerFactory.class, XOTransactionManager.class, PlatformTransactionManager.class })
@EnableTransactionManagement
public class XOAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(PlatformTransactionManager.class)
    public XOTransactionManager transactionManager(XOManagerFactory xoManagerFactory,
            ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        XOTransactionManager transactionManager = new XOTransactionManager(xoManagerFactory);
        transactionManagerCustomizers.ifAvailable((customizers) -> customizers.customize(transactionManager));
        return transactionManager;
    }

    @Bean
    public XOManager getXOManager(XOManagerFactory xoManagerFactory) {
        return (XOManager) Proxy.newProxyInstance(XOAutoConfiguration.class.getClassLoader(), new Class<?>[] { XOManager.class },
                new XOInvocationHandler(xoManagerFactory));
    }

    @RequiredArgsConstructor
    private static class XOInvocationHandler implements InvocationHandler {

        private final XOManagerFactory xoManagerFactory;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            XOManagerHolder xoManagerHolder = (XOManagerHolder) TransactionSynchronizationManager.getResource(xoManagerFactory);
            if (xoManagerHolder == null) {
                throw new XOException("There is no XOManager associated with the current transaction.");
            }
            XOManager xoManager = xoManagerHolder.getXOManager();
            try {
                return method.invoke(xoManager, args);
            } catch (XOException e) {
                throw e;
            } catch (ReflectiveOperationException e) {
                throw new XOException("Cannot invoke method " + method + " on transactional XOManager instance " + xoManager, e);
            }
        }
    }
}
