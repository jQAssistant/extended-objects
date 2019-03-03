package com.buschmais.xo.neo4j.doc.relation.self;

import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.From;
import com.buschmais.xo.neo4j.api.annotation.Relation.To;

// tag::Class[]
@Relation
public interface References {

    @From
    Movie getReferencing();

    @To
    Movie getReferenced();

    int getMinute();

    void setMinute(int minute);

    int getSecond();

    void setSecond(int second);
}
// end::Class[]
