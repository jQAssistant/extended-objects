package com.buschmais.xo.neo4j.test.query;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractXOManagerTest;
import com.buschmais.xo.neo4j.test.query.composite.A;
import com.buschmais.xo.neo4j.test.query.composite.InstanceByValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static com.buschmais.xo.api.Query.Result;
import static com.buschmais.xo.api.Query.Result.CompositeRowObject;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class RestQueryTest extends AbstractXOManagerTest {

    private A a1;
    private A a2_1;
    private A a2_2;

    public RestQueryTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(asList(Database.REST), asList(A.class));
    }

    @Before
    public void createData() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        a1 = xoManager.create(A.class);
        a1.setValue("A1");
        a2_1 = xoManager.create(A.class);
        a2_1.setValue("A2");
        a2_2 = xoManager.create(A.class);
        a2_2.setValue("A2");
        xoManager.currentTransaction().commit();
    }

    @Test
    public void instanceParameter() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        Result<CompositeRowObject> row = xoManager.createQuery("match (a:A) where id(a)={instance} return a").withParameter("instance", a1).execute();
        A a = row.getSingleResult().get("a", A.class);
        assertThat(a, equalTo(a1));
        xoManager.currentTransaction().commit();
    }

}
