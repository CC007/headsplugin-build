package com.github.cc007.headsplugin.config.properties;

import lombok.Data;

@Data
public class ConfigProperties {

    private String version;
    private HeadspluginProperties headsplugin = new HeadspluginProperties();
    private Profiler profiler = new Profiler();
    private Database database = new Database();

    @Data
    public static class Profiler {
        private String defaultLogLevel;
    }

    @Data
    public static class Database {

        /**
         * The chunk size that should be used for queries that make use of the IN keyword.
         *
         * There is a limit to how many elements can be used after an IN keyword in a query.
         * Therefore, it is needed to perform those queries in chunks.
         * This property will define the size of those chunks that should be used.
         * A lower value will require more queries to be executed, which impacts performance.
         */
        private int chunkSize;
    }

}
