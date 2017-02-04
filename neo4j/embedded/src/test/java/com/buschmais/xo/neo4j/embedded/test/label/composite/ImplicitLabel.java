package com.buschmais.xo.neo4j.embedded.test.label.composite;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label
public interface ImplicitLabel {

    String getString();

    void setString(String string);

}
