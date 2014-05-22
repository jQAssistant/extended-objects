package com.buschmais.xo.neo4j.test.find;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.find.composite.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static com.buschmais.xo.api.XOManager.Example;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class FindByExampleTest extends AbstractNeo4jXOManagerTest {

    public FindByExampleTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(A.class);
    }

    @Test
    public void findByExample() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setValue("A1");
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(xoManager.find(A.class, new Example<A>() {
            @Override
            public void prepare(A example) {
                example.setValue("A1");
            }
        }).getSingleResult(), equalTo(a));
    }

}
