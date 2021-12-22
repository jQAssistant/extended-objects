package com.buschmais.xo.neo4j.test.mapping.composite;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.test.inheritance.composite.Version;

@Label("A")
public interface A extends Version {

    @Indexed
    String getIndex();

    void setIndex(String index);

    String getString();

    void setString(String stringValue);

    String[] getStringArray();

    void setStringArray(String[] stringArray);

    char getPrimitiveCharacter();

    void setPrimitiveCharacter(char characterValue);

    char[] getPrimitiveCharacterArray();

    void setPrimitiveCharacterArray(char[] characterValues);

    Character getCharacter();

    void setCharacter(Character characterValue);

    Character[] getCharacterArray();

    void setCharacterArray(Character[] characterValues);

    byte getPrimitiveByte();

    void setPrimitiveByte(byte byteValue);

    byte[] getPrimitiveByteArray();

    void setPrimitiveByteArray(byte[] byteValues);

    Byte getByte();

    void setByte(Byte byteValue);

    Byte[] getByteArray();

    void setByteArray(Byte[] byteValues);

    short getPrimitiveShort();

    void setPrimitiveShort(short shortValue);

    short[] getPrimitiveShortArray();

    void setPrimitiveShortArray(short[] shortValues);

    Short getShort();

    void setShort(Short shortValue);

    Short[] getShortArray();

    void setShortArray(Short[] shortValues);

    int getPrimitiveInteger();

    void setPrimitiveInteger(int integerValue);

    int[] getPrimitiveIntegerArray();

    void setPrimitiveIntegerArray(int[] integerValues);

    Integer getInteger();

    void setInteger(Integer integerValue);

    Integer[] getIntegerArray();

    void setIntegerArray(Integer[] integerValues);

    long getPrimitiveLong();

    void setPrimitiveLong(long longValue);

    long[] getPrimitiveLongArray();

    void setPrimitiveLongArray(long[] longValues);

    Long getLong();

    void setLong(Long longValue);

    Long[] getLongArray();

    void setLongArray(Long[] longValues);

    float getPrimitiveFloat();

    void setPrimitiveFloat(float floatValue);

    float[] getPrimitiveFloatArray();

    void setPrimitiveFloatArray(float[] floatValues);

    Float getFloat();

    void setFloat(Float floatValue);

    Float[] getFloatArray();

    void setFloatArray(Float[] floatValues);

    double getPrimitiveDouble();

    void setPrimitiveDouble(double doubleValue);

    double[] getPrimitiveDoubleArray();

    void setPrimitiveDoubleArray(double[] doubleValues);

    Double getDouble();

    void setDouble(Double doubleValue);

    Double[] getDoubleArray();

    void setDoubleArray(Double[] doubleValues);

    ZonedDateTime getZonedDateTime();

    void setZonedDateTime(ZonedDateTime zonedDateTime);

    @Property("MAPPED_STRING")
    String getMappedString();

    void setMappedString(String mappedString);

    B getB();

    void setB(B b);

    @Relation("MAPPED_B")
    B getMappedB();

    void setMappedB(B mappedB);

    Set<B> getSetOfB();

    @Relation("MAPPED_SET_OF_B")
    Set<B> getMappedSetOfB();

    List<B> getListOfB();

    @Relation("MAPPED_LIST_OF_B")
    List<B> getMappedListOfB();

    Enumeration getEnumeration();

    void setEnumeration(Enumeration enumeration);

    @Property("MAPPED_ENUMERATION")
    Enumeration getMappedEnumeration();

    void setMappedEnumeration(Enumeration enumeration);

}
