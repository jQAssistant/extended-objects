package com.buschmais.xo.neo4j.test.mapping;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.mapping.composite.A;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class PrimitivePropertyMappingIT extends AbstractNeo4JXOManagerIT {

    public PrimitivePropertyMappingIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class);
    }

    @Test
    public void primitiveProperty() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setString("value");
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getString(), equalTo("value"));
        a.setString("updatedValue");
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getString(), equalTo("updatedValue"));
        a.setString(null);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getString(), equalTo(null));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void mappedPrimitiveProperty() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setMappedString("mappedValue");
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        TestResult result = executeQuery("match (a:A) return a.MAPPED_STRING as v");
        assertThat(result.getColumn("v"), hasItem("mappedValue"));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void primitiveArrayProperty() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setStringArray(new String[] { "A", "B" });
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getStringArray(), equalTo(new String[] { "A", "B" }));
        xoManager.currentTransaction().commit();
    }
}
