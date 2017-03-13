package com.buschmais.xo.neo4j.doc.relation.typed;

import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

// tag::Class[]
@Relation
public interface Directed {

    @Outgoing
    Director getDirector();

    @Incoming
    Movie getMovie();

    int getYear();
    void setYear(int year);

}
// end::Class[]
