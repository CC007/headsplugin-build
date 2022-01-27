package com.github.cc007.headsplugin.config.properties;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CategoriesProperties {

    private List<CustomCategoryProperties> custom = new ArrayList<>();
    private Update update = new Update();

    @Data
    public static class CustomCategoryProperties {

        private String name;
        private List<String> searchTerms;
    }

    @Data
    public static class Update {
        private int interval = 5;
    }
}
