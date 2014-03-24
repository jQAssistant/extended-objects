package com.buschmais.xo.neo4j.test.instancelistener;

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.Transaction;
import com.buschmais.xo.api.ValidationMode;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
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
public class InstanceListenerTest extends AbstractCdoManagerTest {

    public InstanceListenerTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(Arrays.asList(A.class, B.class, A2B.class), Arrays.<Class<?>>asList(StaticInstanceListener.class), ValidationMode.AUTO, ConcurrencyMode.SINGLETHREADED, Transaction.TransactionAttribute.MANDATORY);
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
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        B b = XOManager.create(B.class);
        A2B a2b = XOManager.create(a, A2B.class, b);
        assertThat(StaticInstanceListener.getPostCreate(), IsCollectionContaining.<Object>hasItems(a, b, a2b));
        assertThat(StaticInstanceListener.getPreUpdate().isEmpty(), equalTo(true));
        assertThat(StaticInstanceListener.getPostUpdate().isEmpty(), equalTo(true));
        assertThat(StaticInstanceListener.getPreDelete().isEmpty(), equalTo(true));
        assertThat(StaticInstanceListener.getPostDelete().isEmpty(), equalTo(true));
        assertThat(StaticInstanceListener.getPostLoad().isEmpty(), equalTo(true));
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(StaticInstanceListener.getPreUpdate(), IsCollectionContaining.<Object>hasItems(a, b, a2b));
        assertThat(StaticInstanceListener.getPostUpdate(), IsCollectionContaining.<Object>hasItems(a, b, a2b));
        XOManager.currentTransaction().commit();
        closeCdoManager();
        XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        a = XOManager.createQuery("match (a:A) return a", A.class).execute().getSingleResult();
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
        XOManager.flush();
        assertThat(StaticInstanceListener.getPreUpdate(), IsCollectionContaining.<Object>hasItems(a, b, a2b));
        assertThat(StaticInstanceListener.getPostUpdate(), IsCollectionContaining.<Object>hasItems(a, b, a2b));
        XOManager.delete(a2b);
        XOManager.delete(a);
        XOManager.delete(b);
        assertThat(StaticInstanceListener.getPreDelete().size(), equalTo(3));
        assertThat(StaticInstanceListener.getPostDelete().size(), equalTo(3));
        XOManager.currentTransaction().commit();
    }

    @Test
    public void aggregatedLifecycleEvents() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        // @PostCreate
        assertThat(StaticInstanceListener.getAggregated().size(), equalTo(1));
        a.setVersion(1);
        XOManager.currentTransaction().commit();
        // @PreUpdate and @PostUpdate
        assertThat(StaticInstanceListener.getAggregated().size(), equalTo(3));
        closeCdoManager();
        XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        a = XOManager.createQuery("match (a:A) return a", A.class).execute().getSingleResult();
        // @PostLoad
        assertThat(StaticInstanceListener.getAggregated().size(), equalTo(4));
        XOManager.delete(a);
        // @PreDelete and @PostDelete
        assertThat(StaticInstanceListener.getAggregated().size(), equalTo(6));
        XOManager.currentTransaction().commit();
    }

    @Test
    public void typedInstanceListener() {
        XOManager XOManager = getXOManager();
        TypedInstanceListener typedInstanceListener = new TypedInstanceListener();
        XOManager.registerInstanceListener(typedInstanceListener);
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        B b = XOManager.create(B.class);
        A2B a2b = XOManager.create(a, A2B.class, b);
        assertThat(typedInstanceListener.getListOfA(), hasItems(a));
        assertThat(typedInstanceListener.getListOfB(), hasItems(b));
        assertThat(typedInstanceListener.getListOfA2B(), hasItems(a2b));
        XOManager.currentTransaction().commit();
    }
}
