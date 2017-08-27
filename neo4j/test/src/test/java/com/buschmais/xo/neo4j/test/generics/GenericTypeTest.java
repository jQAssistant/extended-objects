package com.buschmais.xo.neo4j.test.generics;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.generics.composite.*;

@RunWith(Parameterized.class)
public class GenericTypeTest extends AbstractNeo4jXOManagerTest {

    public GenericTypeTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(GenericSuperType.class, BoundType.class, Value.class, StringValue.class, ValueContainer.class, StringValueContainer.class);
    }

    @Test
    public void boundPropertyType() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        BoundType b = xoManager.create(BoundType.class);
        b.setValue("value");
        xoManager.currentTransaction().commit();
    }

    @Test
    public void boundCollectionTypeVariable() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        StringValue stringValue = xoManager.create(StringValue.class);
        StringValueContainer stringValueContainer = xoManager.create(StringValueContainer.class);
        stringValueContainer.getValues().add(stringValue);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        List<StringValue> values = stringValueContainer.getValues();
        assertThat(values.size(), equalTo(1));
        assertThat(values.get(0), is(stringValue));
        xoManager.currentTransaction().commit();
    }
}
