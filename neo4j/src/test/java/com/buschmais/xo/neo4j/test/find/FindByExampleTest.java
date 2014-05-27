package com.buschmais.xo.neo4j.test.find;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.api.Example;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.find.composite.A;
import com.buschmais.xo.neo4j.test.find.composite.B;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class FindByExampleTest extends AbstractNeo4jXOManagerTest {

    public FindByExampleTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(A.class, B.class);
    }

    @Test
    public void findByExample() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setValue("A1");
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        // java 7: anonymous inner class
        assertThat(xoManager.find(new Example<A>() {
            @Override
            public void prepare(A example) {
                example.setValue("A1");
            }
        }, A.class).getSingleResult(), equalTo(a));
        // java 8: lambda expression
        assertThat(xoManager.find(example -> example.setValue("A1"), A.class).getSingleResult(), equalTo(a));
    }

    @Test
    public void findCompositeByExample() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        CompositeObject compositeObject = xoManager.create(A.class, B.class);
        compositeObject.as(A.class).setValue("A1");
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        // java 7: anonymous inner class
        assertThat(xoManager.find(new Example<CompositeObject>() {
            @Override
            public void prepare(CompositeObject example) {
                example.as(A.class).setValue("A1");
            }
        }, A.class, B.class).getSingleResult(), equalTo(compositeObject));
        // java 8: lambda expression
        assertThat(xoManager.find(
                example -> example.as(A.class).setValue("A1"),
                A.class, B.class).getSingleResult(), equalTo(compositeObject));

    }

}
