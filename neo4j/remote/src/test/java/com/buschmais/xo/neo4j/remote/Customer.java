package com.buschmais.xo.neo4j.remote;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("Customer")
public interface Customer {

    int getCustomerNo();
    void setCustomerNo(int i);

}
