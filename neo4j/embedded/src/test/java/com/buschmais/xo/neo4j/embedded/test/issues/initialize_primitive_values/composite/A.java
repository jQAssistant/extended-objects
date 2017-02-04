package com.buschmais.xo.neo4j.embedded.test.issues.initialize_primitive_values.composite;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("A")
public interface A {

    boolean isBoolean();

    void setBoolean(boolean bool);

    int getInt();

    void setInt(int i);

    B getB();

    void setB();
}
