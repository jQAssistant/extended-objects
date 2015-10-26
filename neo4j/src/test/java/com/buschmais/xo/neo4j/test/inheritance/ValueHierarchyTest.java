package com.buschmais.xo.neo4j.test.inheritance;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;
import java.util.Collection;

import com.buschmais.xo.neo4j.test.inheritance.composite.*;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;

@Ignore
@RunWith(Parameterized.class)
public class ValueHierarchyTest extends AbstractNeo4jXOManagerTest {

    public ValueHierarchyTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(ObjectDescriptor.class, ArrayDescriptor.class, ValueDescriptor.class, ObjectValueDescriptor.class, ArrayValueDescriptor.class);
    }

    @Test
    public void anonymousSubType() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        ObjectDescriptor master = xoManager.create(ObjectDescriptor.class);
        ObjectDescriptor detail = xoManager.create(ObjectDescriptor.class);
        ObjectValueDescriptor objectValue = xoManager.create(ObjectValueDescriptor.class);
        objectValue.setValue(detail);
        master.getValues().add(objectValue);

        xoManager.currentTransaction().commit();
    }

}
