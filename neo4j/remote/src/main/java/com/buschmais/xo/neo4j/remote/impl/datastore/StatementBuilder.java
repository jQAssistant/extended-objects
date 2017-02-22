package com.buschmais.xo.neo4j.remote.impl.datastore;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.v1.Record;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.remote.impl.model.AbstractRemotePropertyContainer;
import com.buschmais.xo.neo4j.remote.impl.model.StatementExecutor;

public class StatementBuilder {

    private StatementExecutor statementExecutor;

    private Map<AbstractRemotePropertyContainer<?>, String> identifiers;
    private Map<String, Object> parameters;

    private StringBuilder matchBuilder;
    private StringBuilder whereBuilder;
    private StringBuilder createBuilder;
    private StringBuilder setBuilder;
    private StringBuilder deleteBuilder;
    private StringBuilder returnBuilder;

    public StatementBuilder(StatementExecutor statementExecutor) {
        this.statementExecutor = statementExecutor;
        identifiers = new HashMap<>();
        parameters = new HashMap<>();
        matchBuilder = new StringBuilder();
        whereBuilder = new StringBuilder();
        createBuilder = new StringBuilder();
        setBuilder = new StringBuilder();
        deleteBuilder = new StringBuilder();
        returnBuilder = new StringBuilder();
    }

    public String doMatchWhere(String matchExpression, AbstractRemotePropertyContainer<?> entity) {
        String identifier = identifiers.get(entity);
        if (identifier == null) {
            identifier = "e" + identifiers.size();
            identifiers.put(entity, identifier);
            parameters.put(identifier, entity.getId());
            separate(matchBuilder, ",");
            matchBuilder.append(String.format(matchExpression, identifier));
            separate(whereBuilder, " and ");
            whereBuilder.append(String.format("id(%s)={%s}", identifier, identifier));
        }
        return identifier;
    }

    public void doCreate(String expression) {
        separate(createBuilder, " ");
        createBuilder.append("CREATE ").append(expression);
    }

    public void doDelete(String expression) {
        separate(deleteBuilder, " ");
        deleteBuilder.append("DELETE ").append(expression);
    }

    public StatementBuilder doSet(String expression) {
        separate(setBuilder, ",");
        setBuilder.append(expression);
        return this;
    }

    public StatementBuilder doReturn(String s) {
        separate(returnBuilder, ",");
        returnBuilder.append(s);
        return this;
    }

    public StatementBuilder parameter(String parameter, Map<String, Object> properties) {
        parameters.put(parameter, properties);
        return this;
    }

    public String build() {
        StringBuilder statement = new StringBuilder("\n");
        if (matchBuilder.length() > 0) {
            statement.append("MATCH ").append(matchBuilder).append(" ");
        }
        if (whereBuilder.length() > 0) {
            statement.append("WHERE ").append(whereBuilder).append(" ");
        }
        if (createBuilder.length() > 0) {
            statement.append(createBuilder).append(" ");
        }
        if (deleteBuilder.length() > 0) {
            statement.append(deleteBuilder).append(" ");
        }
        if (setBuilder.length() > 0) {
            statement.append("SET ").append(setBuilder).append(" ");
        }
        if (returnBuilder.length() > 0) {
            statement.append("RETURN ").append(returnBuilder);
        }
        return statement.toString();
    }

    public void execute() {
        if (!identifiers.isEmpty()) {
            doReturn("count(*) as count");
            Record record = statementExecutor.getSingleResult(build(), parameters);
            long nodes = record.get("count").asLong();
            if (nodes != 1) {
                throw new XOException("Cannot flush properties.");
            }
        }
    }

    @Override
    public String toString() {
        return "'" + build() + "' " + parameters;
    }

    private void separate(StringBuilder builder, String separator) {
        if (builder.length() > 0) {
            builder.append(separator);
        }
    }

}
