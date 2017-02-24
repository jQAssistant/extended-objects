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

public class StatementExecutor {

    enum StatementLogger {
        TRACE {
            @Override
            void log(String message) {
                LOGGER.trace(message);
            }
        },
        DEBUG {
            @Override
            void log(String message) {
                LOGGER.debug(message);
            }
        },
        INFO {
            @Override
            void log(String message) {
                LOGGER.info(message);
            }
        },
        WARN {
            @Override
            void log(String message) {
                LOGGER.warn(message);
            }
        },
        ERROR {
            @Override
            void log(String message) {
                LOGGER.error(message);
            }
        };

        abstract void log(String message);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(StatementExecutor.class);

    private RemoteDatastoreTransaction transaction;

    private StatementLogger statementLogger = StatementLogger.DEBUG;

    public StatementExecutor(RemoteDatastoreTransaction transaction) {
        this.transaction = transaction;
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
        statementLogger.log("'" + statement + "': " + parameters);
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
