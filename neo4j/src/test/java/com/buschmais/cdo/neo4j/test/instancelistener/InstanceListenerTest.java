package com.buschmais.cdo.neo4j.test.instancelistener;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.ConcurrencyMode;
import com.buschmais.cdo.api.Transaction;
import com.buschmais.cdo.api.ValidationMode;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.instancelistener.composite.*;
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

    public InstanceListenerTest(CdoUnit cdoUnit) {
        super(cdoUnit);
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
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        B b = cdoManager.create(B.class);
        A2B a2b = cdoManager.create(a, A2B.class, b);
        assertThat(StaticInstanceListener.getPostCreate(), IsCollectionContaining.<Object>hasItems(a, b, a2b));
        assertThat(StaticInstanceListener.getPreUpdate().isEmpty(), equalTo(true));
        assertThat(StaticInstanceListener.getPostUpdate().isEmpty(), equalTo(true));
        assertThat(StaticInstanceListener.getPreDelete().isEmpty(), equalTo(true));
        assertThat(StaticInstanceListener.getPostDelete().isEmpty(), equalTo(true));
        assertThat(StaticInstanceListener.getPostLoad().isEmpty(), equalTo(true));
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(StaticInstanceListener.getPreUpdate(), IsCollectionContaining.<Object>hasItems(a, b, a2b));
        assertThat(StaticInstanceListener.getPostUpdate(), IsCollectionContaining.<Object>hasItems(a, b, a2b));
        cdoManager.currentTransaction().commit();
        closeCdoManager();
        cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        a = cdoManager.createQuery("match (a:A) return a", A.class).execute().getSingleResult();
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
        cdoManager.flush();
        assertThat(StaticInstanceListener.getPreUpdate(), IsCollectionContaining.<Object>hasItems(a, b, a2b));
        assertThat(StaticInstanceListener.getPostUpdate(), IsCollectionContaining.<Object>hasItems(a, b, a2b));
        cdoManager.delete(a2b);
        cdoManager.delete(a);
        cdoManager.delete(b);
        assertThat(StaticInstanceListener.getPreDelete().size(), equalTo(3));
        assertThat(StaticInstanceListener.getPostDelete().size(), equalTo(3));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void aggregatedLifecycleEvents() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        // @PostCreate
        assertThat(StaticInstanceListener.getAggregated().size(), equalTo(1));
        a.setVersion(1);
        cdoManager.currentTransaction().commit();
        // @PreUpdate and @PostUpdate
        assertThat(StaticInstanceListener.getAggregated().size(), equalTo(3));
        closeCdoManager();
        cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        a = cdoManager.createQuery("match (a:A) return a", A.class).execute().getSingleResult();
        // @PostLoad
        assertThat(StaticInstanceListener.getAggregated().size(), equalTo(4));
        cdoManager.delete(a);
        // @PreDelete and @PostDelete
        assertThat(StaticInstanceListener.getAggregated().size(), equalTo(6));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void typedInstanceListener() {
        CdoManager cdoManager = getCdoManager();
        TypedInstanceListener typedInstanceListener = new TypedInstanceListener();
        cdoManager.registerInstanceListener(typedInstanceListener);
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        B b = cdoManager.create(B.class);
        A2B a2b = cdoManager.create(a, A2B.class, b);
        assertThat(typedInstanceListener.getListOfA(), hasItems(a));
        assertThat(typedInstanceListener.getListOfB(), hasItems(b));
        assertThat(typedInstanceListener.getListOfA2B(), hasItems(a2b));
        cdoManager.currentTransaction().commit();
    }
}