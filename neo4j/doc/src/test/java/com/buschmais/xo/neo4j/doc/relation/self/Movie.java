package com.buschmais.xo.neo4j.doc.relation.self;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

// tag::Class[]
@Label
public interface Movie {

    @Outgoing
    List<References> getReferenced();

    @Incoming
    List<References> getReferencedBy();

}
// end::Class[]
