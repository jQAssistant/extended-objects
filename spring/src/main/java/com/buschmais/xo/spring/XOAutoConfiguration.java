package com.buschmais.xo.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.XOManagerFactory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
    public XOTransactionManager transactionManager(XOManagerFactory<?, ?, ?, ?> xoManagerFactory) {
        return new XOTransactionManager(xoManagerFactory);
    }

    @Bean
    public XOManager getXOManager(XOManagerFactory<?, ?, ?, ?> xoManagerFactory) {
        return (XOManager) Proxy.newProxyInstance(XOAutoConfiguration.class.getClassLoader(), new Class<?>[] { XOManager.class },
            new XOInvocationHandler(xoManagerFactory));
    }

    private static class XOInvocationHandler implements InvocationHandler {

        private final XOManagerFactory<?, ?, ?, ?> xoManagerFactory;

        private final Map<Method, Function<Object[], Object>> methodInvovationHandlers = new HashMap<>();

        private XOInvocationHandler(XOManagerFactory<?, ?, ?, ?> xoManagerFactory) {
            this.xoManagerFactory = xoManagerFactory;
            try {
                methodInvovationHandlers.put(XOManager.class.getMethod("close"), args -> null);
                methodInvovationHandlers.put(Object.class.getMethod("toString"), args -> "Transactional XOManager Proxy:" + getXOManagerHolder());
                methodInvovationHandlers.put(Object.class.getMethod("hashCode"), args -> System.identityHashCode(this));
                methodInvovationHandlers.put(Object.class.getMethod("equals", Object.class), args -> super.equals(args[0]));
            } catch (NoSuchMethodException e) {
                throw new XOException("Cannot initialize transactional XOManager proxy", e);
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            Function<Object[], Object> methodInvocationHandler = this.methodInvovationHandlers.getOrDefault(method, x -> {
                XOManagerHolder xoManagerHolder = getXOManagerHolder();
                if (xoManagerHolder == null) {
                    throw new XOException("There is no XOManager associated with the current transaction.");
                }
                XOManager xoManager = xoManagerHolder.getXOManager();
                try {
                    return method.invoke(xoManager, args);
                } catch (ReflectiveOperationException e) {
                    throw new XOException("Cannot invoke method " + method + " on transactional XOManager instance " + xoManager, e);
                }
            });
            return methodInvocationHandler.apply(args);
        }

        private XOManagerHolder getXOManagerHolder() {
            return (XOManagerHolder) TransactionSynchronizationManager.getResource(xoManagerFactory);
        }
    }
}
