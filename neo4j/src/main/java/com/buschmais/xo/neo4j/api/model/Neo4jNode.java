package com.buschmais.xo.neo4j.api.model;

import org.neo4j.graphdb.*;

public class Neo4jNode extends AbstractNeo4jPropertyContainer<Node> implements Node {

    public Neo4jNode(Node delegate) {
        super(delegate);
    }

    public long getId() {
        return delegate.getId();
    }

    public void delete() {
        delegate.delete();
    }

    public Iterable<Relationship> getRelationships() {
        return delegate.getRelationships();
    }

    public boolean hasRelationship() {
        return delegate.hasRelationship();
    }

    public Iterable<Relationship> getRelationships(RelationshipType... types) {
        return delegate.getRelationships(types);
    }

    public Iterable<Relationship> getRelationships(Direction direction, RelationshipType... types) {
        return delegate.getRelationships(direction, types);
    }

    public boolean hasRelationship(RelationshipType... types) {
        return delegate.hasRelationship(types);
    }

    public boolean hasRelationship(Direction direction, RelationshipType... types) {
        return delegate.hasRelationship(direction, types);
    }

    public Iterable<Relationship> getRelationships(Direction dir) {
        return delegate.getRelationships(dir);
    }

    public boolean hasRelationship(Direction dir) {
        return delegate.hasRelationship(dir);
    }

    public Iterable<Relationship> getRelationships(RelationshipType type, Direction dir) {
        return delegate.getRelationships(type, dir);
    }

    public boolean hasRelationship(RelationshipType type, Direction dir) {
        return delegate.hasRelationship(type, dir);
    }

    public Relationship getSingleRelationship(RelationshipType type, Direction dir) {
        return delegate.getSingleRelationship(type, dir);
    }

    public Relationship createRelationshipTo(Node otherNode, RelationshipType type) {
        return delegate.createRelationshipTo(otherNode, type);
    }

    public Iterable<RelationshipType> getRelationshipTypes() {
        return delegate.getRelationshipTypes();
    }

    public int getDegree() {
        return delegate.getDegree();
    }

    public int getDegree(RelationshipType type) {
        return delegate.getDegree(type);
    }

    public int getDegree(Direction direction) {
        return delegate.getDegree(direction);
    }

    public int getDegree(RelationshipType type, Direction direction) {
        return delegate.getDegree(type, direction);
    }

    @Deprecated
    public Traverser traverse(Traverser.Order traversalOrder, StopEvaluator stopEvaluator, ReturnableEvaluator returnableEvaluator,
            RelationshipType relationshipType, Direction direction) {
        return delegate.traverse(traversalOrder, stopEvaluator, returnableEvaluator, relationshipType, direction);
    }

    @Deprecated
    public Traverser traverse(Traverser.Order traversalOrder, StopEvaluator stopEvaluator, ReturnableEvaluator returnableEvaluator,
            RelationshipType firstRelationshipType, Direction firstDirection, RelationshipType secondRelationshipType, Direction secondDirection) {
        return delegate.traverse(traversalOrder, stopEvaluator, returnableEvaluator, firstRelationshipType, firstDirection, secondRelationshipType,
                secondDirection);
    }

    @Deprecated
    public Traverser traverse(Traverser.Order traversalOrder, StopEvaluator stopEvaluator, ReturnableEvaluator returnableEvaluator,
            Object... relationshipTypesAndDirections) {
        return delegate.traverse(traversalOrder, stopEvaluator, returnableEvaluator, relationshipTypesAndDirections);
    }

    public void addLabel(Label label) {
        delegate.addLabel(label);
    }

    public void removeLabel(Label label) {
        delegate.removeLabel(label);
    }

    public boolean hasLabel(Label label) {
        return delegate.hasLabel(label);
    }

    public Iterable<Label> getLabels() {
        return delegate.getLabels();
    }
}
