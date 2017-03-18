package com.buschmais.xo.neo4j.remote.impl.datastore;

import java.util.Map;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.StatementRunner;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.exceptions.Neo4jException;
import org.neo4j.driver.v1.exceptions.NoSuchRecordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.spi.logging.LogLevel;

public class StatementExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatementExecutor.class);

    private RemoteDatastoreTransaction transaction;

    private StatementConfig statementConfig;

    public StatementExecutor(RemoteDatastoreTransaction transaction, StatementConfig statementConfig) {
        this.transaction = transaction;
        this.statementConfig = statementConfig;
    }

    public Record getSingleResult(String statement, Value parameters) {
        return getSingleResult(statement, parameters.asMap());
    }

    public Record getSingleResult(String statement, Map<String, Object> parameters) {
        try {
            return getSingleResult(execute(statement, parameters));
        } catch (Neo4jException e) {
            throw new XOException("Cannot get result for statement '" + statement + "', " + parameters, e);
        }
    }

    public StatementResult execute(String statement, Value parameters) {
        return execute(statement, parameters.asMap());
    }

    public StatementResult execute(String statement, Map<String, Object> parameters) {
        LogLevel statementLogger = statementConfig.getLogLevel();
        if (!LogLevel.NONE.equals(statementLogger)) {
            statementLogger.log(LOGGER, "'" + statement + "': " + parameters);
        }
        try {
            StatementRunner statementRunner = transaction.getStatementRunner();
            return statementRunner.run(statement, parameters);
        } catch (Neo4jException e) {
            throw new XOException("Cannot execute statement '" + statement + "', " + parameters, e);
        }
    }

    private Record getSingleResult(StatementResult result) {
        try {
            return result.single();
        } catch (NoSuchRecordException e) {
            throw new XOException("Query returned no result.");
        } finally {
            result.consume();
        }
    }

}
