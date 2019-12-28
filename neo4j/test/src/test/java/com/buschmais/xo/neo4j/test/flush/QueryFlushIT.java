package com.buschmais.xo.neo4j.test.flush;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.Neo4jDatabase;
import com.buschmais.xo.neo4j.test.flush.composite.A;
import com.buschmais.xo.neo4j.test.flush.composite.FindByNameQuery;
import com.buschmais.xo.neo4j.test.flush.composite.FlushRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class QueryFlushIT extends AbstractNeo4JXOManagerIT {

    @Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(singletonList(Neo4jDatabase.BOLT), asList(A.class, FlushRepository.class));
    }

    public QueryFlushIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Test
    public void flush() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        FlushRepository repository = xoManager.getRepository(FlushRepository.class);
        A a = xoManager.create(A.class);
        a.setName("1");
        assertThat(xoManager.createQuery("MATCH (a:A) WHERE a.name='1' RETURN a").execute().getSingleResult().get("a", A.class), equalTo(a));
        a.setName("2");
        assertThat(xoManager.createQuery(FindByNameQuery.class).withParameter("name", "1").execute().hasResult(), equalTo(true));
        assertThat(repository.findByName("1"), equalTo(a));
        assertThat(a.findByName("1"), equalTo(a));
        assertThat(xoManager.createQuery("MATCH (a:A) WHERE a.name='1' RETURN a").flush(false).execute().hasResult(), equalTo(true));
        xoManager.flush();
        assertThat(xoManager.createQuery(FindByNameQuery.class).withParameter("name", "1").execute().hasResult(), equalTo(false));
        assertThat(repository.findByName("1"), nullValue());
        assertThat(a.findByName("1"), nullValue());
        assertThat(xoManager.createQuery("MATCH (a:A) WHERE a.name='1' RETURN a").flush(false).execute().hasResult(), equalTo(false));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void overwriteFlush() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setName("1");
        assertThat(xoManager.createQuery("MATCH (a:A) WHERE a.name='1' RETURN a").execute().getSingleResult().get("a", A.class), equalTo(a));
        a.setName("2");
        assertThat(xoManager.createQuery(FindByNameQuery.class).flush(true).withParameter("name", "2").execute().hasResult(), equalTo(true));
        xoManager.currentTransaction().commit();
    }

}
