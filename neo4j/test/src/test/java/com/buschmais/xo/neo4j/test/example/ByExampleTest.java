package com.buschmais.xo.neo4j.test.example;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.api.Example;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.example.composite.A;
import com.buschmais.xo.neo4j.test.example.composite.B;
import com.buschmais.xo.neo4j.test.example.composite.Parent;

@RunWith(Parameterized.class)
public class ByExampleTest extends AbstractNeo4jXOManagerTest {

    public ByExampleTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(A.class, B.class, Parent.class);
    }

    @Test
    public void createByExample() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        // java 7: anonymous inner class
        A a1 = xoManager.create(new Example<A>() {
            @Override
            public void prepare(A example) {
                example.setValue("A1");
                example.setName("Name of A1");
            }
        }, A.class);
        assertThat(a1.getValue(), equalTo("A1"));
        assertThat(a1.getName(), equalTo("Name of A1"));
        // java 8: lambda expression
        A a2 = xoManager.create(example -> {
            example.setValue("A2");
            example.setName("Name of A2");
        }, A.class);
        assertThat(a2.getValue(), equalTo("A2"));
        assertThat(a2.getName(), equalTo("Name of A2"));
        // Create a relation
        Parent parent = xoManager.create(example -> example.setName("Name of A1->A2"), a1, Parent.class, a2);
        assertThat(parent.getName(), equalTo("Name of A1->A2"));

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
        assertThat(xoManager.find(example -> example.setValue("A2"), A.class).getSingleResult().getParent().getName(), equalTo("Name of A1->A2"));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void findCompositeByExample() {
        XOManager xoManager = getXOManager();
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
        // java 8: lambda expression, alternative
        xoManager.create(A.class, example -> example.setValue("A3"));
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
        // java 8: lambda expression, alternative
        assertThat(xoManager.find(A.class, example -> example.setValue("A1")).getSingleResult(), equalTo(compositeObject1));
    }

}
