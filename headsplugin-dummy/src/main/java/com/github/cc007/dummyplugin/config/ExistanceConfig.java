package com.github.cc007.dummyplugin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExistanceConfig {
    @Bean
    public boolean doIExist(){
        return true;
    }
}
