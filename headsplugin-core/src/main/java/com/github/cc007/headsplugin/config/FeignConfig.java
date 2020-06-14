package com.github.cc007.headsplugin.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = {"com.github.cc007.headsplugin.integration.rest.clients"})
public class FeignConfig {
}
