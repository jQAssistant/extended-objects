package com.buschmais.xo.neo4j.embedded.test.performance;

public interface ApiUnderTest<Entity, Relation> {

    void begin();

    void commit();

    Entity createEntity();

    void setName(Entity entity, String value);

    Relation createRelation(Entity entity1, Entity entity2);

}
