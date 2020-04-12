package com.github.cc007.headsplugin.api.business.services.heads;

public interface CategoryUpdater {
    void updateCategory(String categoryName);

    void updateCategories();

    void updateCategoriesIfNecessary();
}
