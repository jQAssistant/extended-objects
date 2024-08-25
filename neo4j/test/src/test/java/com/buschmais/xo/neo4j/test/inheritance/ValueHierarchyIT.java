package com.buschmais.xo.neo4j.test.inheritance;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.inheritance.composite.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ValueHierarchyIT extends AbstractNeo4JXOManagerIT {

    public ValueHierarchyIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(ObjectDescriptor.class, ArrayDescriptor.class, ValueDescriptor.class, ObjectValueDescriptor.class, ArrayValueDescriptor.class);
    }

    @Test
    public void valueHierarchy() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        ObjectDescriptor master = xoManager.create(ObjectDescriptor.class);
        ObjectDescriptor detail = xoManager.create(ObjectDescriptor.class);
        ObjectValueDescriptor objectValue = xoManager.create(ObjectValueDescriptor.class);
        objectValue.setValue(detail);
        master.getValues().add(objectValue);

        ObjectDescriptor element = xoManager.create(ObjectDescriptor.class);
        ArrayValueDescriptor arrayValue = xoManager.create(ArrayValueDescriptor.class);
        arrayValue.getValue().add(element);
        master.getValues().add(arrayValue);

        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        TestResult result1 = executeQuery("MATCH (master:Object)-[:HAS_VALUE]->(:Object:Value)-[:IS]->(detail:Object) return master, detail");
        List<ObjectDescriptor> masters1 = result1.<ObjectDescriptor> getColumn("master");
        List<ObjectDescriptor> details1 = result1.<ObjectDescriptor> getColumn("detail");
        assertThat(masters1).hasSize(1);
        assertThat(masters1.get(0)).isEqualTo(master);
        assertThat(details1).hasSize(1);
        assertThat(details1.get(0)).isEqualTo(detail);
        TestResult result2 = executeQuery("MATCH (master:Object)-[:HAS_VALUE]->(:Array:Value)-[:HAS_ELEMENT]->(detail:Object) return master, detail");
        List<ObjectDescriptor> masters2 = result2.<ObjectDescriptor> getColumn("master");
        List<ObjectDescriptor> details2 = result2.<ObjectDescriptor> getColumn("detail");
        assertThat(masters2).hasSize(1);
        assertThat(masters2.get(0)).isEqualTo(master);
        assertThat(details2).hasSize(1);
        assertThat(details2.get(0)).isEqualTo(element);
        xoManager.currentTransaction().commit();
    }

}
