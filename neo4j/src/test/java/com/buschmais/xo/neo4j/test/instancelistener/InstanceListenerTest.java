package com.buschmais.xo.neo4j.test.instancelistener;

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.Transaction;
import com.buschmais.xo.api.ValidationMode;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.instancelistener.composite.*;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class InstanceListenerTest extends AbstractNeo4jXOManagerTest {

    public InstanceListenerTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(Arrays.asList(A.class, B.class, A2B.class), Arrays.<Class<?>>asList(StaticInstanceListener.class), ValidationMode.AUTO, ConcurrencyMode.SINGLETHREADED, Transaction.TransactionAttribute.MANDATORY);
    }

    @Before
    public void reset() {
        StaticInstanceListener.getPostCreate().clear();
        StaticInstanceListener.getPreUpdate().clear();
        StaticInstanceListener.getPostUpdate().clear();
        StaticInstanceListener.getPreDelete().clear();
        StaticInstanceListener.getPostDelete().clear();
        StaticInstanceListener.getPostLoad().clear();
        StaticInstanceListener.getAggregated().clear();
    }

    @Test
    public void staticInstanceListener() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        B b = xoManager.create(B.class);
        A2B a2b = xoManager.create(a, A2B.class, b);
        assertThat(StaticInstanceListener.getPostCreate(), IsCollectionContaining.<Object>hasItems(a, b, a2b));
        assertThat(StaticInstanceListener.getPreUpdate().isEmpty(), equalTo(true));
        assertThat(StaticInstanceListener.getPostUpdate().isEmpty(), equalTo(true));
        assertThat(StaticInstanceListener.getPreDelete().isEmpty(), equalTo(true));
        assertThat(StaticInstanceListener.getPostDelete().isEmpty(), equalTo(true));
        assertThat(StaticInstanceListener.getPostLoad().isEmpty(), equalTo(true));
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(StaticInstanceListener.getPreUpdate(), IsCollectionContaining.<Object>hasItems(a, b, a2b));
        assertThat(StaticInstanceListener.getPostUpdate(), IsCollectionContaining.<Object>hasItems(a, b, a2b));
        xoManager.currentTransaction().commit();
        closeXOmanager();
        xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        a = xoManager.createQuery("match (a:A) return a", A.class).execute().getSingleResult();
        assertThat(StaticInstanceListener.getPostLoad(), IsCollectionContaining.<Object>hasItems(a));
        a2b = a.getA2b();
        assertThat(StaticInstanceListener.getPostLoad(), IsCollectionContaining.<Object>hasItems(a, a2b));
        b = a2b.getB();
        assertThat(StaticInstanceListener.getPostLoad(), IsCollectionContaining.<Object>hasItems(a, a2b, b));
        StaticInstanceListener.getPreUpdate().clear();
        StaticInstanceListener.getPostUpdate().clear();
        a.setVersion(1);
        a2b.setVersion(1);
        b.setVersion(1);
        xoManager.flush();
        assertThat(StaticInstanceListener.getPreUpdate(), IsCollectionContaining.<Object>hasItems(a, b, a2b));
        assertThat(StaticInstanceListener.getPostUpdate(), IsCollectionContaining.<Object>hasItems(a, b, a2b));
        xoManager.delete(a2b);
        xoManager.delete(a);
        xoManager.delete(b);
        assertThat(StaticInstanceListener.getPreDelete().size(), equalTo(3));
        assertThat(StaticInstanceListener.getPostDelete().size(), equalTo(3));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void aggregatedLifecycleEvents() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        // @PostCreate
        assertThat(StaticInstanceListener.getAggregated().size(), equalTo(1));
        a.setVersion(1);
        xoManager.currentTransaction().commit();
        // @PreUpdate and @PostUpdate
        assertThat(StaticInstanceListener.getAggregated().size(), equalTo(3));
        closeXOmanager();
        xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        a = xoManager.createQuery("match (a:A) return a", A.class).execute().getSingleResult();
        // @PostLoad
        assertThat(StaticInstanceListener.getAggregated().size(), equalTo(4));
        xoManager.delete(a);
        // @PreDelete and @PostDelete
        assertThat(StaticInstanceListener.getAggregated().size(), equalTo(6));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void typedInstanceListener() {
        XOManager xoManager = getXoManager();
        TypedInstanceListener typedInstanceListener = new TypedInstanceListener();
        xoManager.registerInstanceListener(typedInstanceListener);
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        B b = xoManager.create(B.class);
        A2B a2b = xoManager.create(a, A2B.class, b);
        assertThat(typedInstanceListener.getListOfA(), hasItems(a));
        assertThat(typedInstanceListener.getListOfB(), hasItems(b));
        assertThat(typedInstanceListener.getListOfA2B(), hasItems(a2b));
        xoManager.currentTransaction().commit();
    }
}
