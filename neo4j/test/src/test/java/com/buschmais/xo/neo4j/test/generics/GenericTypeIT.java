package com.buschmais.xo.neo4j.test.generics;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.generics.composite.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class GenericTypeIT extends AbstractNeo4JXOManagerIT {

    public GenericTypeIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(GenericSuperType.class, BoundType.class, Value.class, StringValue.class, ValueContainer.class, StringValueContainer.class);
    }

    @Test
    public void boundPropertyType() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        BoundType b = xoManager.create(BoundType.class);
        b.setValue("value");
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(b.getValue()).isEqualTo("value");
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
        assertThat(values).hasSize(1);
        assertThat(values.get(0)).isEqualTo(stringValue);
        xoManager.currentTransaction().commit();
    }
}
