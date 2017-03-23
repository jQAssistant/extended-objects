package com.buschmais.xo.neo4j.test.autoflush;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.Neo4jDatabase;
import com.buschmais.xo.neo4j.test.autoflush.composite.A;
import com.buschmais.xo.neo4j.test.autoflush.composite.AutoFlushRepository;
import com.buschmais.xo.neo4j.test.autoflush.composite.FindByNameQuery;

@RunWith(Parameterized.class)
public class AutoFlushTest extends AbstractNeo4jXOManagerTest {

    @Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(singletonList(Neo4jDatabase.BOLT), asList(A.class, AutoFlushRepository.class));
    }

    public AutoFlushTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Test
    public void autoFlush() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        AutoFlushRepository autoFlushRepository = xoManager.getRepository(AutoFlushRepository.class);
        A a = xoManager.create(A.class);
        a.setName("1");
        assertThat(xoManager.createQuery("MATCH (a:A) WHERE a.name='1' RETURN a").execute().getSingleResult().get("a", A.class), equalTo(a));
        a.setName("2");
        assertThat(xoManager.createQuery(FindByNameQuery.class).withParameter("name", "1").execute().hasResult(), equalTo(true));
        assertThat(autoFlushRepository.findByName("1"), equalTo(a));
        assertThat(a.findByName("1"), equalTo(a));
        assertThat(xoManager.createQuery("MATCH (a:A) WHERE a.name='1' RETURN a").autoFlush(false).execute().hasResult(), equalTo(true));
        xoManager.flush();
        assertThat(xoManager.createQuery(FindByNameQuery.class).withParameter("name", "1").execute().hasResult(), equalTo(false));
        assertThat(autoFlushRepository.findByName("1"), nullValue());
        assertThat(a.findByName("1"), nullValue());
        assertThat(xoManager.createQuery("MATCH (a:A) WHERE a.name='1' RETURN a").autoFlush(false).execute().hasResult(), equalTo(false));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void overwriteAutoFlush() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setName("1");
        assertThat(xoManager.createQuery("MATCH (a:A) WHERE a.name='1' RETURN a").execute().getSingleResult().get("a", A.class), equalTo(a));
        a.setName("2");
        assertThat(xoManager.createQuery(FindByNameQuery.class).autoFlush(true).withParameter("name", "2").execute().hasResult(), equalTo(true));
        xoManager.currentTransaction().commit();
    }

}
