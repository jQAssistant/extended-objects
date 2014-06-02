package com.buschmais.xo.neo4j.test.example;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.api.Example;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.example.composite.A;
import com.buschmais.xo.neo4j.test.example.composite.B;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class ByExampleTest extends AbstractNeo4jXOManagerTest {

    public ByExampleTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(A.class, B.class);
    }

    @Test
    public void createByExample() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        // java 7: anonymous inner class
        A a1 = xoManager.create(new Example<A>() {
            @Override
            public void prepare(A example) {
                example.setValue("A1");
            }
        }, A.class);
        // java 8: lambda expression
        xoManager.create(example -> example.setValue("a2"), A.class);
        xoManager.currentTransaction().commit();

        xoManager.currentTransaction().begin();
        // java 7: anonymous inner class
        assertThat(xoManager.find(new Example<A>() {
            @Override
            public void prepare(A example) {
                example.setValue("A1");
            }
        }, A.class).getSingleResult(), equalTo(a1));
        // java 8: lambda expression
        assertThat(xoManager.find(example -> example.setValue("A1"), A.class).getSingleResult(), equalTo(a1));
    }

    @Test
    public void findCompositeByExample() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        // java 7: anonymous inner class
        CompositeObject compositeObject1 = xoManager.<CompositeObject>create(new Example<CompositeObject>() {
            @Override
            public void prepare(CompositeObject example) {
                example.as(A.class).setValue("A1");
            }
        }, A.class, B.class);
        // java 8: lambda expression
        xoManager.<CompositeObject>create(example -> example.as(A.class).setValue("A2"), A.class, B.class);
        xoManager.currentTransaction().commit();

        xoManager.currentTransaction().begin();
        // java 7: anonymous inner class
        assertThat(xoManager.find(new Example<CompositeObject>() {
            @Override
            public void prepare(CompositeObject example) {
                example.as(A.class).setValue("A1");
            }
        }, A.class, B.class).getSingleResult(), equalTo(compositeObject1));
        // java 8: lambda expression
        assertThat(xoManager.find(
                example -> example.as(A.class).setValue("A1"),
                A.class, B.class).getSingleResult(), equalTo(compositeObject1));

    }

}
