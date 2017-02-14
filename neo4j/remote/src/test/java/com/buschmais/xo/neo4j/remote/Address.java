package com.buschmais.xo.neo4j.remote;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Created by dirk.mahler on 13.02.2017.
 */
@Label("Address")
public interface Address {

    String getCity();
    void setCity(String city);

}



