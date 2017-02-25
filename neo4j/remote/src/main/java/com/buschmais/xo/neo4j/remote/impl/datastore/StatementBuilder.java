package com.buschmais.xo.neo4j.remote.impl.datastore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.driver.v1.Record;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.remote.impl.model.AbstractRemotePropertyContainer;

public class StatementBuilder {

    private StatementExecutor statementExecutor;

    private Set<String> identifiers = new HashSet<>();
    private Map<AbstractRemotePropertyContainer<?>, String> entityIdentifiers = new HashMap<>();
    private Map<String, Object> parameters = new HashMap<>();

    private StringBuilder matchBuilder = new StringBuilder();
    private StringBuilder whereBuilder = new StringBuilder();
    private StringBuilder createBuilder = new StringBuilder();
    private StringBuilder deleteBuilder = new StringBuilder();
    private StringBuilder setBuilder = new StringBuilder();
    private StringBuilder removeBuilder = new StringBuilder();
    private StringBuilder returnBuilder = new StringBuilder();

    public StatementBuilder(StatementExecutor statementExecutor) {
        this.statementExecutor = statementExecutor;
    }

    public String doMatch(String matchExpression, String prefix) {
        String identifier = createIdentifier(prefix);
        separate(matchBuilder, ",");
        matchBuilder.append(String.format(matchExpression, identifier));
        return identifier;
    }

    public String doMatchWhere(String matchExpression, AbstractRemotePropertyContainer<?> entity, String prefix) {
        String identifier = entityIdentifiers.get(entity);
        if (identifier == null) {
            identifier = createIdentifier(prefix);
            entityIdentifiers.put(entity, identifier);
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

    public StatementBuilder doRemove(String expression) {
        separate(removeBuilder, ",");
        removeBuilder.append(expression);
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
        StringBuilder statement = new StringBuilder();
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
        if (removeBuilder.length() > 0) {
            statement.append("REMOVE ").append(removeBuilder).append(" ");
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
            long count = record.get("count").asLong();
            if (count != 1) {
                throw new XOException("Cannot flush properties.");
            }
        }
    }

    @Override
    public String toString() {
        return "'" + build() + "' " + parameters;
    }

    private String createIdentifier(String prefix) {
        String identifier = prefix + identifiers.size();
        identifiers.add(identifier);
        return identifier;
    }

    private void separate(StringBuilder builder, String separator) {
        if (builder.length() > 0) {
            builder.append(separator);
        }
    }

}
