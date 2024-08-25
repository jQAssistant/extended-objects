package com.buschmais.xo.neo4j.test.mapping;

import java.time.ZonedDateTime;
import java.util.Collection;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.mapping.composite.A;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class PrimitivePropertyMappingIT extends AbstractNeo4JXOManagerIT {

    public static final ZonedDateTime ZONED_DATE_TIME = ZonedDateTime.now();

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
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        setPropertyValues(a, 'v', "value", 0, ZONED_DATE_TIME);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        verifyPropertyValues(a, 'v', "value", 0, ZONED_DATE_TIME);
        setPropertyValues(a, 'u', "updatedValue", 1, ZONED_DATE_TIME.plus(1, DAYS));
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        verifyPropertyValues(a, 'u', "updatedValue", 1, ZONED_DATE_TIME.plus(1, DAYS));
        a.setString(null);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(a.getString()).isNull();
        xoManager.currentTransaction()
            .commit();
    }

    private void setPropertyValues(A a, char characterValue, String stringValue, int value, ZonedDateTime zonedDateTimeValue) {
        a.setCharacter(characterValue);
        a.setPrimitiveCharacter(characterValue);
        a.setString(stringValue);
        a.setByte(Byte.valueOf((byte) value));
        a.setPrimitiveByte((byte) value);
        a.setShort(Short.valueOf((short) value));
        a.setPrimitiveShort((short) value);
        a.setInteger(value);
        a.setPrimitiveInteger(value);
        a.setLong((long) value);
        a.setPrimitiveLong(value);
        a.setFloat(Float.valueOf(value));
        a.setPrimitiveFloat(value);
        a.setDouble(Double.valueOf(value));
        a.setPrimitiveDouble(value);
        a.setZonedDateTime(zonedDateTimeValue);
    }

    private void verifyPropertyValues(A a, char expectedCharacterValue, String expectedStringValue, int expectedValue,
        ZonedDateTime expectedZonedDateTimeValue) {
        assertThat(a.getCharacter()).isEqualTo(expectedCharacterValue);
        assertThat(a.getPrimitiveCharacter()).isEqualTo(expectedCharacterValue);
        assertThat(a.getString()).isEqualTo(expectedStringValue);
        assertThat(a.getByte()).isEqualTo((byte) expectedValue);
        assertThat(a.getPrimitiveByte()).isEqualTo((byte) expectedValue);
        assertThat(a.getShort()).isEqualTo((short) expectedValue);
        assertThat(a.getPrimitiveShort()).isEqualTo((short) expectedValue);
        assertThat(a.getInteger()).isEqualTo(expectedValue);
        assertThat(a.getPrimitiveInteger()).isEqualTo(expectedValue);
        assertThat(a.getLong()).isEqualTo((long) expectedValue);
        assertThat(a.getPrimitiveLong()).isEqualTo((long) expectedValue);
        assertThat(a.getFloat()).isEqualTo((float) expectedValue);
        assertThat(a.getPrimitiveFloat()).isEqualTo((float) expectedValue);
        assertThat(a.getDouble()).isEqualTo((double) expectedValue);
        assertThat(a.getPrimitiveDouble()).isEqualTo((double) expectedValue);
        assertThat(a.getZonedDateTime()).isEqualTo(expectedZonedDateTimeValue);
    }

    @Test
    public void mappedPrimitiveProperty() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        a.setMappedString("mappedValue");
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        TestResult result = executeQuery("match (a:A) return a.MAPPED_STRING as v");
        assertThat(result.getColumn("v")).contains("mappedValue");
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void primitiveArrayProperty() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        a.setCharacterArray(new Character[] { 'A', 'B' });
        a.setPrimitiveCharacterArray(new char[] { 'A', 'B' });
        a.setStringArray(new String[] { "A", "B" });
        a.setByteArray(new Byte[] { (byte) 0, (byte) 1 });
        a.setPrimitiveByteArray(new byte[] { (byte) 0, (byte) 1 });
        a.setShortArray(new Short[] { (short) 0, (short) 1 });
        a.setPrimitiveShortArray(new short[] { (short) 0, (short) 1 });
        a.setIntegerArray(new Integer[] { 0, 1 });
        a.setPrimitiveIntegerArray(new int[] { 0, 1 });
        a.setLongArray(new Long[] { 0l, 1l });
        a.setPrimitiveLongArray(new long[] { 0l, 1l });
        a.setFloatArray(new Float[] { 0f, 1f });
        a.setPrimitiveFloatArray(new float[] { 0f, 1f });
        a.setDoubleArray(new Double[] { 0d, 1d });
        a.setPrimitiveDoubleArray(new double[] { 0d, 1d });
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(a.getCharacterArray()).isEqualTo(new Character[] { 'A', 'B' });
        assertThat(a.getPrimitiveCharacterArray()).isEqualTo(new char[] { 'A', 'B' });
        assertThat(a.getStringArray()).isEqualTo(new String[] { "A", "B" });
        assertThat(a.getByteArray()).isEqualTo(new Byte[] { (byte) 0, (byte) 1 });
        assertThat(a.getPrimitiveByteArray()).isEqualTo(new byte[] { (byte) 0, (byte) 1 });
        assertThat(a.getShortArray()).isEqualTo(new Short[] { (short) 0, (short) 1 });
        assertThat(a.getPrimitiveShortArray()).isEqualTo(new short[] { (short) 0, (short) 1 });
        assertThat(a.getIntegerArray()).isEqualTo(new Integer[] { 0, 1 });
        assertThat(a.getPrimitiveIntegerArray()).isEqualTo(new int[] { 0, 1 });
        assertThat(a.getLongArray()).isEqualTo(new Long[] { 0l, 1l });
        assertThat(a.getPrimitiveLongArray()).isEqualTo(new long[] { 0l, 1l });
        assertThat(a.getFloatArray()).isEqualTo(new Float[] { 0f, 1f });
        assertThat(a.getPrimitiveFloatArray()).isEqualTo(new float[] { 0f, 1f });
        assertThat(a.getDoubleArray()).isEqualTo(new Double[] { 0d, 1d });
        assertThat(a.getPrimitiveDoubleArray()).isEqualTo(new double[] { 0d, 1d });
        xoManager.currentTransaction()
            .commit();
    }
}
