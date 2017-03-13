package com.buschmais.xo.neo4j.doc.relation.self;

import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

// tag::Class[]
@Relation
public interface References {

    @Outgoing
    Movie getReferencing();

    @Incoming
    Movie getReferenced();

    int getMinute();
    void setMinute(int minute);

    int getSecond();
    void setSecond(int second);
}
// end::Class[]
