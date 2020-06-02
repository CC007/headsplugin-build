package com.github.cc007.headsplugin.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {"com.github.cc007.headsplugin"},
        excludeName = "dev.alangomes.springspigot.SpringSpigotAutoConfiguration"
)
public class Application {
    public static void main(String[] args) {
    }
}