package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.Profiler;
import com.github.cc007.headsplugin.api.business.services.heads.CategoryUpdater;
import com.github.cc007.headsplugin.api.business.services.heads.HeadUpdater;
import com.github.cc007.headsplugin.api.business.services.heads.HeadUtils;
import com.github.cc007.headsplugin.config.properties.CategoriesProperties;
import com.github.cc007.headsplugin.integration.daos.interfaces.Categorizable;
import com.github.cc007.headsplugin.integration.daos.services.CategorizableUtils;
import com.github.cc007.headsplugin.integration.database.repositories.CategoryRepository;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;
import com.github.cc007.headsplugin.integration.database.transaction.Transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.logging.log4j.Level;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
public class CategoryUpdaterImpl implements CategoryUpdater {

    private final HeadUpdater headUpdater;
    private final CategorizableUtils categorizableUtils;
    private final HeadUtils headUtils;
    private final CategoryRepository categoryRepository;
    private final DatabaseRepository databaseRepository;
    private final CategoriesProperties categoriesProperties;
    private final Transaction transaction;
    private final Profiler profiler;

    @Override
    public void updateCategory(String categoryName) throws IllegalArgumentException {
        transaction.runTransacted(() -> {
            val foundHeadsBySource = requestCategoryHeads(categoryName);
            if (headUtils.isEmpty(foundHeadsBySource)) {
                log.warn("No heads found for category " + categoryName + ". Skipping the update");
                return;
            }
            updateCategory(categoryName, foundHeadsBySource);
        });
    }

    @Override
    public void updateCategories() {
        profiler.runProfiled(Level.INFO, "Done updating all categories", () -> {
            val categoryMap = categorizableUtils.getCategoryMap();
            //noinspection CodeBlock2Expr
            transaction.runTransacted(() -> {
                updateCategories(categoryMap.keySet());
            });
        });
    }

    @Override
    public void updateCategoriesIfNecessary() {
        profiler.runProfiled(Level.INFO, "Done updating necessary categories", () -> {
            val categoryMap = categorizableUtils.getCategoryMap();
            transaction.runTransacted(() -> {
                val categoriesToBeUpdated = getCategoriesToBeUpdated(categoryMap.keySet());
                log.debug("Found categories to be updated: " + categoriesToBeUpdated);
                updateCategories(categoriesToBeUpdated);
            });
        });
    }

    @Override
    public Collection<String> getUpdatableCategoryNames(boolean necessaryOnly) {
        Set<String> categoryNames = categorizableUtils.getCategoryMap().keySet();
        return necessaryOnly ? getCategoriesToBeUpdated(categoryNames) : categoryNames;
    }

    /**
     * Get the category names for categories that need to be updated, based on the category update interval
     *
     * @param categoryNames the names of the categories for which to check if they need to be updated.
     * @return the filtered set of category names
     */
    private Collection<String> getCategoriesToBeUpdated(Collection<String> categoryNames) {
        int categoryUpdateInterval = categoriesProperties.getUpdate().getInterval();
        return profiler.runProfiled("Done filtering categories to be updated", () ->
                categoryNames.stream()
                        .filter(categoryName -> categoryRepository.findByOrCreateFromName(categoryName)
                                .getLastUpdated()
                                .plusHours(categoryUpdateInterval)
                                .isBefore(LocalDateTime.now()))
                        .collect(Collectors.toSet())
        );
    }

    /**
     * Updates all categories for the given category names.
     *
     * @param categoryNames the category names of the categories to update
     */
    private void updateCategories(Collection<String> categoryNames) {
        categoryNames.forEach(categoryName -> {
            val foundHeadsBySource = requestCategoryHeads(categoryName);
            if (headUtils.isEmpty(foundHeadsBySource)) {
                log.warn("No heads found for category " + categoryName + ". Skipping this category");
                return;
            }
            log.info("Updating category: " + categoryName);
            updateCategory(categoryName, foundHeadsBySource);
        });
    }

    /**
     * Request the heads for a given category from the categorizables sources
     *
     * @param categoryName the name of the category to request heads for
     * @return the heads for that category, grouped by source
     */
    private Map<String, List<Head>> requestCategoryHeads(String categoryName) {
        return categorizableUtils.getCategoryMap()
                .get(categoryName)
                .stream()
                .collect(Collectors.toMap(
                        Categorizable::getSource,
                        categorizable -> categorizable.getCategoryHeads(categoryName)
                ));
    }

    /**
     * Update a category for the given category name with the given heads.
     * <p>
     * First all heads that weren't already in the database will be added.
     * Then all heads will be linked to the given category and data source (like MineSkin), if it wasn't already.
     * This includes both all new heads and the ones that were already in the database.
     * The category will also be linked to this source, if it wasn't already
     *
     * @param categoryName  the category name of the category to update
     * @param headsBySource the heads for that category, grouped by source
     */
    private void updateCategory(String categoryName, Map<String, List<Head>> headsBySource) {
        val category = categoryRepository.findByOrCreateFromName(categoryName);
        headsBySource.forEach((source, heads) -> {
            val storedHeads = headUpdater.updateHeads(heads);
            storedHeads.forEach(category::addhead);
            val database = databaseRepository.findByOrCreateFromName(source);
            storedHeads.forEach(database::addhead);
            database.addCategory(category);
        });
        category.setLastUpdated(LocalDateTime.now());
    }

}
