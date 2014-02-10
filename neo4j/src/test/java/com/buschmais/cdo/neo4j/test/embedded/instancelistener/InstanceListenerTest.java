package com.buschmais.cdo.neo4j.test.embedded.instancelistener;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.instancelistener.composite.A;
import com.buschmais.cdo.neo4j.test.embedded.instancelistener.composite.A2B;
import com.buschmais.cdo.neo4j.test.embedded.instancelistener.composite.B;
import com.buschmais.cdo.neo4j.test.embedded.instancelistener.composite.InstanceListener;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class InstanceListenerTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class, B.class, A2B.class};
    }

    @Override
    protected List<Class<?>> getInstanceListenerTypes() {
        return Arrays.<Class<?>>asList(InstanceListener.class);
    }

    @Test
    public void staticInstanceListener() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        B b = cdoManager.create(B.class);
        A2B a2b = cdoManager.create(a, A2B.class, b);
        assertThat(InstanceListener.getPostCreate(), IsCollectionContaining.<Object>hasItems(a, b, a2b));
        assertThat(InstanceListener.getPreUpdate().isEmpty(), equalTo(true));
        assertThat(InstanceListener.getPostUpdate().isEmpty(), equalTo(true));
        assertThat(InstanceListener.getPreDelete().isEmpty(), equalTo(true));
        assertThat(InstanceListener.getPostDelete().isEmpty(), equalTo(true));
        assertThat(InstanceListener.getPostLoad().isEmpty(), equalTo(true));
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(InstanceListener.getPreUpdate(), IsCollectionContaining.<Object>hasItems(a, b, a2b));
        assertThat(InstanceListener.getPostUpdate(), IsCollectionContaining.<Object>hasItems(a, b, a2b));
        cdoManager.currentTransaction().commit();
        closeCdoManager();
        cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        a = cdoManager.createQuery("match (a:A) return a", A.class).execute().getSingleResult();
        assertThat(InstanceListener.getPostLoad(), IsCollectionContaining.<Object>hasItems(a));
        a2b = a.getA2b();
        assertThat(InstanceListener.getPostLoad(), IsCollectionContaining.<Object>hasItems(a, a2b));
        b = a2b.getB();
        assertThat(InstanceListener.getPostLoad(), IsCollectionContaining.<Object>hasItems(a, a2b, b));
        InstanceListener.getPreUpdate().clear();
        InstanceListener.getPostUpdate().clear();
        a.setVersion(1);
        a2b.setVersion(1);
        b.setVersion(1);
        cdoManager.flush();
        assertThat(InstanceListener.getPreUpdate(), IsCollectionContaining.<Object>hasItems(a, b, a2b));
        assertThat(InstanceListener.getPostUpdate(), IsCollectionContaining.<Object>hasItems(a, b, a2b));
        cdoManager.delete(a2b);
        cdoManager.delete(a);
        cdoManager.delete(b);
        assertThat(InstanceListener.getPreDelete().size(), equalTo(3));
        assertThat(InstanceListener.getPostDelete().size(), equalTo(3));
        cdoManager.currentTransaction().commit();
    }
}
