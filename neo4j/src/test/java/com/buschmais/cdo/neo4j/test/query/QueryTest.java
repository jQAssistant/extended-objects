package com.buschmais.cdo.neo4j.test.query;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.query.composite.A;
import com.buschmais.cdo.neo4j.test.query.composite.InstanceByValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static com.buschmais.cdo.api.Query.Result;
import static com.buschmais.cdo.api.Query.Result.CompositeRowObject;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class QueryTest extends AbstractCdoManagerTest {

    private A a1;
    private A a2_1;
    private A a2_2;

    public QueryTest(CdoUnit cdoUnit) {
        super(cdoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(asList(Database.MEMORY), asList(A.class));
    }

    @Before
    public void createData() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        a1 = cdoManager.create(A.class);
        a1.setValue("A1");
        a2_1 = cdoManager.create(A.class);
        a2_1.setValue("A2");
        a2_2 = cdoManager.create(A.class);
        a2_2.setValue("A2");
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void cypherStringQuery() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        Result<CompositeRowObject> result = cdoManager.createQuery("match (a:A) where a.value={value} return a").withParameter("value", "A1").execute();
        A a = result.getSingleResult().get("a", A.class);
        assertThat(a.getValue(), equalTo("A1"));
        result = cdoManager.createQuery("match (a:A) where a.Value={value} return a").withParameter("value", "A2").execute();
        try {
            result.getSingleResult().get("a", A.class);
            fail("Expecting a " + CdoException.class.getName());
        } catch (CdoException e) {
        }
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void cypherStringQuerySimple() {
        CdoManager cdoManager = getCdoManager();

        cdoManager.currentTransaction().begin();
        Result<CompositeRowObject> result = cdoManager.createQuery("MATCH (a:A) RETURN a.value LIMIT 1").execute();
        assertEquals("A1", result.getSingleResult().as(String.class));

        Result<CompositeRowObject> longResult = cdoManager.createQuery("MATCH (a:A) RETURN 10 LIMIT 1").execute();
        assertEquals(10L, (long) longResult.getSingleResult().as(Long.class));

        cdoManager.currentTransaction().commit();
    }

    @Test
    public void compositeRowTypedQuery() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        Result<InstanceByValue> result = cdoManager.createQuery(InstanceByValue.class).withParameter("value", "A1").execute();
        A a = result.getSingleResult().getA();
        assertThat(a.getValue(), equalTo("A1"));
        result = cdoManager.createQuery(InstanceByValue.class).withParameter("value", "A2").execute();
        try {
            result.getSingleResult().getA();
            fail("Expecting a " + CdoException.class.getName());
        } catch (CdoException e) {
        }
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void typedQuery() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        Result<InstanceByValue> result = cdoManager.createQuery(InstanceByValue.class).withParameter("value", "A1").execute();
        A a = result.getSingleResult().getA();
        assertThat(a.getValue(), equalTo("A1"));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void instanceParameter() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        Result<CompositeRowObject> row = cdoManager.createQuery("match (a:A) where a={instance} return a").withParameter("instance", a1).execute();
        A a = row.getSingleResult().get("a", A.class);
        assertThat(a, equalTo(a1));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void optionalMatch() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        Result<CompositeRowObject> row = getCdoManager().createQuery("OPTIONAL MATCH (a:A) WHERE a.name = 'X' return a").execute();
        A a = row.getSingleResult().get("a", A.class);
        assertThat(a, equalTo(null));
        cdoManager.currentTransaction().commit();
    }

}
