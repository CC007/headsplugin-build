package com.github.cc007.headsplugin.config.properties;

import lombok.Data;

@Data
public class HeadspluginProperties {

    private Provider provider = new Provider();
    private boolean suppressHttpClientErrors = true;
    private CategoriesProperties categories = new CategoriesProperties();
    private Search search = new Search();

    @Data
    public static class Provider {

        private boolean freshcoal = true;
        private boolean mineskin = true;
        private boolean minecraftHeads = true;
    }

    @Data
    public static class Search {

        private Update update = new Update();

        @Data
        public static class Update {

            private int interval = 1440;
        }
    }
}
