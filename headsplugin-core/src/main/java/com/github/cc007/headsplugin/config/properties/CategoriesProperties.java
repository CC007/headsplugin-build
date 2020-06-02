package com.github.cc007.headsplugin.config.properties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@ConfigurationProperties(prefix = "headsplugin.categories")
@Data
public class CategoriesProperties {

    @Getter(AccessLevel.NONE)
    private List<LinkedHashMap<String, ?>> custom = new ArrayList<>();
    private Update update = new Update();

    /**
     * Due to unknown reasons, the LinkedHashMap doesn't automatically get converted to a CustomCategory.
     * Therefore a custom getter is implemented, using a custom constructor for CustomCategory, using that LinkedHashMap
     *
     * @return the list of CustomCategory objects
     */
    public List<CustomCategory> getCustom() {
        return custom.stream().map(CustomCategory::new).collect(Collectors.toList());
    }

    @Data
    public static class CustomCategory {

        /**
         * A custom constructor is made to accomodate the shortcomings of the object converters for lists of objects
         *
         * @param customCategoryMap the map that is actually stored as a field in {@link CategoriesProperties}.
         */
        public CustomCategory(LinkedHashMap<String, ?> customCategoryMap) {
            this.name = (String) customCategoryMap.get("name");
            this.searchTerms = (List<String>) customCategoryMap.get("searchTerms");
        }

        private String name;
        private List<String> searchTerms;
    }

    @Data
    public static class Update {
        private int interval = 5;
    }
}
