package com.buschmais.xo.neo4j.remote;

import java.util.List;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

@Label
public interface Person extends CompositeObject {

    @Indexed
    String getName();

    void setName(String name);

    @Relation("HAS_ADDRESS")
    List<Address> getAddresses();

    @Relation("HAS_PRIMARY_ADDRESS")
    Address getPrimaryAddress();

    void setPrimaryAddress(Address addresses);
}
