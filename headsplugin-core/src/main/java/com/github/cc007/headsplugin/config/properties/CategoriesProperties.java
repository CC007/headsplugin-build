package com.github.cc007.headsplugin.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "headsplugin.categories")
@Data
public class CategoriesProperties {

    private List<CustomCategory> custom = new ArrayList<>();
    private Update update = new Update();

    @Data
    public static class CustomCategory {
        private String name;
    }

    @Data
    public static class Update {
        private int interval = 5;
    }
}
