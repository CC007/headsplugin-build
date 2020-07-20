package com.github.cc007.headsplugin.config;

import com.github.cc007.headsplugin.config.properties.CategoriesProperties;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"com.github.cc007.headsplugin"})
@EnableConfigurationProperties(CategoriesProperties.class)
public class Application {
    public static void main(String[] args) {
    }
}