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
        a.setCharacter('v');
        a.setString("value");
        a.setByte((byte) 0);
        a.setShort((short) 0);
        a.setInteger(0);
        a.setLong(0l);
        a.setFloat(0f);
        a.setDouble(0d);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getCharacter(), equalTo('v'));
        assertThat(a.getString(), equalTo("value"));
        assertThat(a.getByte(), equalTo((byte) 0));
        assertThat(a.getShort(), equalTo((short) 0));
        assertThat(a.getInteger(), equalTo(0));
        assertThat(a.getLong(), equalTo(0l));
        assertThat(a.getFloat(), equalTo(0f));
        assertThat(a.getDouble(), equalTo(0d));
        a.setCharacter('u');
        a.setString("updatedValue");
        a.setByte((byte) 1);
        a.setShort((short) 1);
        a.setInteger(1);
        a.setLong(1l);
        a.setFloat(1f);
        a.setDouble(1d);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getCharacter(), equalTo('u'));
        assertThat(a.getString(), equalTo("updatedValue"));
        assertThat(a.getByte(), equalTo((byte) 1));
        assertThat(a.getShort(), equalTo((short) 1));
        assertThat(a.getInteger(), equalTo(1));
        assertThat(a.getLong(), equalTo(1l));
        assertThat(a.getFloat(), equalTo(1f));
        assertThat(a.getDouble(), equalTo(1d));
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
        // a.setCharacterArray(new char[] { 'A', 'B' });
        a.setStringArray(new String[] { "A", "B" });
        a.setByteArray(new byte[] { (byte) 0, (byte) 1 });
        // a.setShortArray(new short[] { (short) 0, (short) 1 });
        a.setIntegerArray(new int[] { 0, 1 });
        a.setLongArray(new long[] { 0l, 1l });
        a.setFloatArray(new float[] { 0f, 1f });
        a.setDoubleArray(new double[] { 0d, 1d });
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        // assertThat(a.getCharacterArray(), equalTo(new char[] { 'A', 'B' }));
        assertThat(a.getStringArray(), equalTo(new String[] { "A", "B" }));
        assertThat(a.getByteArray(), equalTo(new byte[] { (byte) 0, (byte) 1 }));
        // assertThat(a.getShortArray(), equalTo(new short[] { (short) 0, (short) 1 }));
        assertThat(a.getIntegerArray(), equalTo(new int[] { 0, 1 }));
        assertThat(a.getLongArray(), equalTo(new long[] { 0l, 1l }));
        assertThat(a.getFloatArray(), equalTo(new float[] { 0f, 1f }));
        assertThat(a.getDoubleArray(), equalTo(new double[] { 0d, 1d }));
        xoManager.currentTransaction().commit();
    }
}
