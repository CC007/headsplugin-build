package com.github.cc007.headsplugin.api.business.services.heads;

public interface CategoryUpdater {
    /**
     * Update the specified category
     *
     * @param categoryName the category to update
     * @throws IllegalArgumentException when an unknown category name is provided.
     */
    void updateCategory(String categoryName) throws IllegalArgumentException;

    /**
     * Update all categories.
     */
    void updateCategories();

    /**
     * Update all categories that haven't recently been updated. This depends on the property headsplugin.categories.update.interval in config.yml
     */
    void updateCategoriesIfNecessary();
}
