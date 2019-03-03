package com.buschmais.xo.neo4j.doc.relation.typed;

import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.From;
import com.buschmais.xo.neo4j.api.annotation.Relation.To;

// tag::Class[]
@Relation
public interface Directed {

    @From
    Director getDirector();

    @To
    Movie getMovie();

    int getYear();

    void setYear(int year);

}
// end::Class[]
