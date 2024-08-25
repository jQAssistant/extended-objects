package com.buschmais.xo.neo4j.test.instancelistener;

import java.util.Arrays;
import java.util.Collection;

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.Transaction;
import com.buschmais.xo.api.ValidationMode;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.instancelistener.composite.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class InstanceListenerIT extends AbstractNeo4JXOManagerIT {

    public InstanceListenerIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(Arrays.asList(A.class, B.class, A2B.class), Arrays.<Class<?>>asList(StaticInstanceListener.class), ValidationMode.AUTO,
            ConcurrencyMode.SINGLETHREADED, Transaction.TransactionAttribute.MANDATORY);
    }

    @Before
    public void reset() {
        StaticInstanceListener.getPostCreate()
            .clear();
        StaticInstanceListener.getPreUpdate()
            .clear();
        StaticInstanceListener.getPostUpdate()
            .clear();
        StaticInstanceListener.getPreDelete()
            .clear();
        StaticInstanceListener.getPostDelete()
            .clear();
        StaticInstanceListener.getPostLoad()
            .clear();
        StaticInstanceListener.getAggregated()
            .clear();
    }

    @Test
    public void staticInstanceListener() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        B b = xoManager.create(B.class);
        A2B a2b = xoManager.create(a, A2B.class, b);
        assertThat(StaticInstanceListener.getPostCreate()).contains(a, b, a2b);
        assertThat(StaticInstanceListener.getPreUpdate()).isEmpty();
        assertThat(StaticInstanceListener.getPostUpdate()).isEmpty();
        assertThat(StaticInstanceListener.getPreDelete()).isEmpty();
        assertThat(StaticInstanceListener.getPostDelete()).isEmpty();
        assertThat(StaticInstanceListener.getPostLoad()).isEmpty();
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(StaticInstanceListener.getPreUpdate()).contains(a, b, a2b);
        assertThat(StaticInstanceListener.getPostUpdate()).contains(a, b, a2b);
        xoManager.currentTransaction()
            .commit();
        closeXOmanager();
        xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        a = xoManager.createQuery("match (a:A) return a", A.class)
            .execute()
            .getSingleResult();
        assertThat(StaticInstanceListener.getPostLoad()).contains(a);
        a2b = a.getA2b();
        assertThat(StaticInstanceListener.getPostLoad()).contains(a, a2b);
        b = a2b.getB();
        assertThat(StaticInstanceListener.getPostLoad()).contains(a, a2b, b);
        StaticInstanceListener.getPreUpdate()
            .clear();
        StaticInstanceListener.getPostUpdate()
            .clear();
        a.setVersion(1);
        a2b.setVersion(1);
        b.setVersion(1);
        xoManager.flush();
        assertThat(StaticInstanceListener.getPreUpdate()).contains(a, b, a2b);
        assertThat(StaticInstanceListener.getPostUpdate()).contains(a, b, a2b);
        xoManager.delete(a2b);
        xoManager.delete(a);
        xoManager.delete(b);
        assertThat(StaticInstanceListener.getPreDelete()).hasSize(3);
        assertThat(StaticInstanceListener.getPostDelete()).hasSize(3);
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void aggregatedLifecycleEvents() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        // @PostCreate
        assertThat(StaticInstanceListener.getAggregated()).hasSize(1);
        a.setVersion(1);
        xoManager.currentTransaction()
            .commit();
        // @PreUpdate and @PostUpdate
        assertThat(StaticInstanceListener.getAggregated()).hasSize(3);
        closeXOmanager();
        xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        a = xoManager.createQuery("match (a:A) return a", A.class)
            .execute()
            .getSingleResult();
        // @PostLoad
        assertThat(StaticInstanceListener.getAggregated()).hasSize(4);
        xoManager.delete(a);
        // @PreDelete and @PostDelete
        assertThat(StaticInstanceListener.getAggregated()).hasSize(6);
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void typedInstanceListener() {
        XOManager xoManager = getXOManager();
        TypedInstanceListener typedInstanceListener = new TypedInstanceListener();
        xoManager.registerInstanceListener(typedInstanceListener);
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        B b = xoManager.create(B.class);
        A2B a2b = xoManager.create(a, A2B.class, b);
        assertThat(typedInstanceListener.getListOfA()).contains(a);
        assertThat(typedInstanceListener.getListOfB()).contains(b);
        assertThat(typedInstanceListener.getListOfA2B()).contains(a2b);
        xoManager.currentTransaction()
            .commit();
    }
}
