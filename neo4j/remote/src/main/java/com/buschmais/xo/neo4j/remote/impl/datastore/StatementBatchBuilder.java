package com.buschmais.xo.neo4j.remote.impl.datastore;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.Value;

public class StatementBatchBuilder {

    private StatementExecutor statementExecutor;

    private Map<String, List<Value>> batches = new HashMap<>();

    public StatementBatchBuilder(StatementExecutor statementExecutor) {
        this.statementExecutor = statementExecutor;
    }

    public void add(String key, Value parameters) {
        List<Value> batch = batches.get(key);
        if (batch == null) {
            batch = new LinkedList<>();
            batches.put(key, batch);
        }
        batch.add(parameters);
    }

    public void execute() {
        for (Map.Entry<String, List<Value>> entry : batches.entrySet()) {
            String statement = entry.getKey();
            List<Value> batch = entry.getValue();
            String batchStatement = "UNWIND {batch} as entry " + statement + " RETURN count(*) as count";
            statementExecutor.getSingleResult(batchStatement, parameters("batch", batch));
            // TODO verify result
        }
        batches.clear();
    }
}
