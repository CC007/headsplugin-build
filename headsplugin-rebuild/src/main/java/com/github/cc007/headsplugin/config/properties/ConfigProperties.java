package com.github.cc007.headsplugin.config.properties;

import lombok.Data;

@Data
public class ConfigProperties {

    private String version;
    private HeadspluginProperties headsplugin = new HeadspluginProperties();
    private Profiler profiler = new Profiler();

    @Data
    public static class Profiler {
        private String defaultLogLevel;
    }

}
