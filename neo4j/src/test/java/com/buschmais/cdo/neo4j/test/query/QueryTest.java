package com.buschmais.cdo.neo4j.test.query;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.query.composite.A;
import com.buschmais.cdo.neo4j.test.query.typedquery.InstanceByValue;
import org.junit.Before;
import org.junit.Test;

import static com.buschmais.cdo.api.Query.Result;
import static com.buschmais.cdo.api.Query.Result.CompositeRowObject;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class QueryTest extends AbstractCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class};
    }

    @Before
    public void createData() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.begin();
        A a1 = cdoManager.create(A.class);
        a1.setValue("A1");
        A a2_1 = cdoManager.create(A.class);
        a2_1.setValue("A2");
        A a2_2 = cdoManager.create(A.class);
        a2_2.setValue("A2");
        cdoManager.commit();
    }

    @Test
    public void cypherStringQuery() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.begin();
        Result<CompositeRowObject> result= cdoManager.createQuery("match (a:A) where a.value={value} return a").withParameter("value", "A1").execute();
        A a = result.getSingleResult().get("a", A.class);
        assertThat(a.getValue(), equalTo("A1"));
        result = cdoManager.createQuery("match (a:A) where a.Value={value} return a").withParameter("value", "A2").execute();
        try {
            result.getSingleResult().get("a", A.class);
            fail("Expecting a " + CdoException.class.getName());
        } catch (CdoException e) {
        }
        cdoManager.commit();
    }

    @Test
    public void rowTypedQuery() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.begin();
        Result<CompositeRowObject> result = cdoManager.createQuery(InstanceByValue.class).withParameter("value", "A1").execute();
        A a = result.getSingleResult().get("a", A.class);
        assertThat(a.getValue(), equalTo("A1"));
        result = cdoManager.createQuery(InstanceByValue.class).withParameter("value", "A2").execute();
        try {
            result.getSingleResult().get("a", A.class);
            fail("Expecting a " + CdoException.class.getName());
        } catch (CdoException e) {
        }
        cdoManager.commit();
    }

    @Test
    public void typedQuery() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.begin();
        Result<InstanceByValue> result = cdoManager.createQuery(InstanceByValue.class).withParameter("value", "A1").execute();
        A a = result.getSingleResult().getA();
        assertThat(a.getValue(), equalTo("A1"));
        cdoManager.commit();
    }
}
