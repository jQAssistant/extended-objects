package com.buschmais.xo.neo4j.remote.impl.datastore;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Value;

public class StatementBatchBuilder {

    private static class BatchEntry {

        private List<Value> batch = new ArrayList<>();
        private Callback callback;

        public BatchEntry(Callback callback) {
            this.callback = callback;
        }

        void add(Value parameters) {
            batch.add(parameters);
        }

        public List<Value> getBatch() {
            return batch;
        }

        public Callback getCallback() {
            return callback;
        }

    }

    @FunctionalInterface
    public interface Callback {

        void process(Record result);

    }

    private StatementExecutor statementExecutor;

    private Map<String, BatchEntry> batches = new HashMap<>();

    public StatementBatchBuilder(StatementExecutor statementExecutor) {
        this.statementExecutor = statementExecutor;
    }

    public void add(String key, Value parameters) {
        add(key, parameters, null);
    }

    public void add(String key, Value parameters, Callback callback) {
        BatchEntry batch = batches.get(key);
        if (batch == null) {
            batch = new BatchEntry(callback);
            batches.put(key, batch);
        }
        batch.add(parameters);
    }

    public void execute() {
        for (Map.Entry<String, BatchEntry> entry : batches.entrySet()) {
            String statement = entry.getKey();
            BatchEntry batchEntry = entry.getValue();
            List<Value> batch = batchEntry.getBatch();
            if (!batch.isEmpty()) {
                String batchStatement = "UNWIND {batch} as entry " + statement;
                Record result = statementExecutor.getSingleResult(batchStatement, parameters("batch", batch));
                Callback callback = batchEntry.getCallback();
                if (callback != null) {
                    callback.process(result);
                }
            }
        }
        batches.clear();
    }

}
