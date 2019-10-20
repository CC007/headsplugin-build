package com.github.cc007.headsplugin.config;

import com.github.cc007.headsplugin.business.services.chat.ChatManager;
import dev.alangomes.springspigot.configuration.DynamicValue;
import dev.alangomes.springspigot.configuration.Instance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VersionConfig {
    @DynamicValue("${version}")
    private Instance<String> version;

    @Autowired
    private ChatManager chatManager;

    @Bean
    public String pluginVersion() {
        return version.get();
    }
}
