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

    @Builder.Default
    private LogLevel logLevel = LogLevel.NONE;

    @Builder.Default
    private boolean batchableDefault = true;

    public static class StatementConfigBuilder {
        // needed to add this for an issue between Javadoc and Lombo, see https://stackoverflow.com/a/58809436
    }
}
