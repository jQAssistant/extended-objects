package com.buschmais.xo.neo4j.test.label.composite;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("EXPLICIT_LABEL")
public interface ExplicitLabel {

    String getString();

    void setString(String string);

}
