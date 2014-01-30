package com.buschmais.cdo.neo4j.test.embedded.label.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label("EXPLICIT_LABEL")
public interface ExplicitLabel {

    String getString();

    void setString(String string);

}
