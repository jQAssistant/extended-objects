package com.buschmais.xo.neo4j.remote.impl.datastore;

import static lombok.AccessLevel.PRIVATE;

import com.buschmais.xo.spi.logging.LogLevel;

import lombok.*;

@Builder(toBuilder = true)
@Getter
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
@ToString
public class StatementConfig {

    private static final StatementConfig PROTOTYPE = new StatementConfig();

    public static final StatementConfig.StatementConfigBuilder builder() {
        return PROTOTYPE.toBuilder();
    }

    private LogLevel logLevel = LogLevel.NONE;

    private boolean batchableDefault = true;
}
