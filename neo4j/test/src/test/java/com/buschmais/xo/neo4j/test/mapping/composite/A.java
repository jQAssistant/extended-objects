package com.buschmais.xo.neo4j.test.mapping.composite;

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

    char getCharacter();

    void setCharacter(char characterValue);

    char[] getCharacterArray();

    void setCharacterArray(char[] characterValues);

    byte getByte();

    void setByte(byte byteValue);

    byte[] getByteArray();

    void setByteArray(byte[] byteValues);

    short getShort();

    void setShort(short shortValue);

    short[] getShortArray();

    void setShortArray(short[] shortValues);

    int getInteger();

    void setInteger(int integerValue);

    int[] getIntegerArray();

    void setIntegerArray(int[] integerValues);

    long getLong();

    void setLong(long longValue);

    long[] getLongArray();

    void setLongArray(long[] longValues);

    float getFloat();

    void setFloat(float floatValue);

    float[] getFloatArray();

    void setFloatArray(float[] floatValues);

    double getDouble();

    void setDouble(double doubleValue);

    double[] getDoubleArray();

    void setDoubleArray(double[] doubleValues);

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
