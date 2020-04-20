package com.buschmais.xo.neo4j.remote.impl.datastore;

import java.util.Map;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.spi.logging.LogLevel;

import org.neo4j.driver.QueryRunner;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Value;
import org.neo4j.driver.exceptions.Neo4jException;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatementExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatementExecutor.class);

    private RemoteDatastoreTransaction transaction;

    private StatementConfig statementConfig;

    public StatementExecutor(RemoteDatastoreTransaction transaction, StatementConfig statementConfig) {
        this.transaction = transaction;
        this.statementConfig = statementConfig;
    }

    public Record getSingleResult(String statement, Value parameters) {
        try {
            return getSingleResult(execute(statement, parameters.asMap()));
        } catch (Neo4jException e) {
            throw new XOException("Cannot get result for statement '" + statement + "', " + parameters.asMap(), e);
        }
    }

    public Result execute(String statement, Value parameters) {
        return execute(statement, parameters.asMap());
    }

    public Result execute(String statement, Map<String, Object> parameters) {
        LogLevel statementLogger = statementConfig.getLogLevel();
        if (!LogLevel.NONE.equals(statementLogger)) {
            statementLogger.log(LOGGER, "'" + statement + "': " + parameters);
        }
        try {
            QueryRunner queryRunner = transaction.getQueryRunner();
            return queryRunner.run(statement, parameters);
        } catch (Neo4jException e) {
            throw new XOException("Cannot execute statement '" + statement + "', " + parameters, e);
        }
    }

    private Record getSingleResult(Result result) {
        try {
            return result.single();
        } catch (NoSuchRecordException e) {
            throw new XOException("Query returned no result.");
        } finally {
            result.consume();
        }
    }

}
