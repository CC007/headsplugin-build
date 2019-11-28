package com.github.cc007.headsplugin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("com.github.cc007.headsplugin.integration.database.repositories")
public class DatabaseConfig {
}
