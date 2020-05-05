package com.github.cc007.headsplugin.config;

import dev.alangomes.springspigot.configuration.DynamicValue;
import dev.alangomes.springspigot.configuration.Instance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VersionConfig {
    @DynamicValue("${version}")
    private Instance<String> version;

    @Bean
    public String pluginVersion() {
        return version.get();
    }
}
