package com.buschmais.xo.neo4j.test.modifier;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.modifier.composite.AbstractType;
import com.buschmais.xo.neo4j.test.modifier.composite.ConcreteType;
import com.buschmais.xo.neo4j.test.modifier.composite.FinalType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class ModifierTest extends AbstractNeo4jXOManagerTest {

    public ModifierTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(AbstractType.class, FinalType.class, ConcreteType.class);
    }

    @Test
    public void abstractModifier() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        try {
            xoManager.create(AbstractType.class);
            fail("Expecting an " + XOException.class.getName());
        } catch (XOException e) {
        }
        CompositeObject compositeObject = xoManager.create(AbstractType.class, ConcreteType.class);
        assertThat(compositeObject, instanceOf(AbstractType.class));
        assertThat(compositeObject, instanceOf(ConcreteType.class));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void finalModifier() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        FinalType finalType = xoManager.create(FinalType.class);
        assertThat(finalType, instanceOf(FinalType.class));
        try {
            xoManager.create(FinalType.class, ConcreteType.class);
            fail("Expecting an " + XOException.class.getName());
        } catch (XOException e) {
        }
        xoManager.currentTransaction().commit();
    }

}
