package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.domain.events.CategoriesUpdatedEvent;
import com.github.cc007.headsplugin.api.business.domain.events.CategoryUpdatedEvent;
import com.github.cc007.headsplugin.api.business.services.Profiler;
import com.github.cc007.headsplugin.api.business.services.heads.CategoryUpdater;
import com.github.cc007.headsplugin.api.business.services.heads.HeadUpdater;
import com.github.cc007.headsplugin.api.business.services.heads.utils.CategoryUtils;
import com.github.cc007.headsplugin.api.business.services.heads.utils.HeadUtils;
import com.github.cc007.headsplugin.config.properties.CategoriesProperties;
import com.github.cc007.headsplugin.integration.daos.interfaces.Categorizable;
import com.github.cc007.headsplugin.integration.database.repositories.CategoryRepository;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;
import com.github.cc007.headsplugin.integration.database.transaction.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
public class CategoryUpdaterImpl implements CategoryUpdater {

    private final HeadUpdater headUpdater;
    private final CategoryUtils categoryUtils;
    private final HeadUtils headUtils;
    private final CategoryRepository categoryRepository;
    private final DatabaseRepository databaseRepository;
    private final Plugin plugin;
    private final CategoriesProperties categoriesProperties;
    private final Transaction transaction;
    private final Profiler profiler;

    @Override
    public void updateCategory(String categoryName) throws IllegalArgumentException {
        transaction.runTransacted(() -> {
            final var foundHeadsBySource = requestCategoryHeads(categoryName);
            if (headUtils.isEmpty(foundHeadsBySource)) {
                log.warn("No heads found for category " + categoryName + ". Skipping the update");
                return;
            }
            updateCategory(categoryName, foundHeadsBySource);
        });
    }

    @Override
    public void updateCategories() {
        final var categoriesToBeUpdatedRef = new AtomicReference<Collection<String>>();
        double duration = profiler.runProfiled(Level.INFO, "Done updating all categories", () -> {
            final var categoryMap = categoryUtils.getCategoryMap();
            final var categoriesToBeUpdated = categoryMap.keySet();
            categoriesToBeUpdatedRef.set(categoriesToBeUpdated);
            //noinspection CodeBlock2Expr
            transaction.runTransacted(() -> {
                updateCategories(categoriesToBeUpdated);
            });
        });
        final var categoriesUpdatedEvent = new CategoriesUpdatedEvent(categoriesToBeUpdatedRef.get(), duration, Instant.now());
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().getPluginManager().callEvent(categoriesUpdatedEvent));
    }

    @Override
    public void updateCategoriesIfNecessary() {
        final var categoriesToBeUpdatedRef = new AtomicReference<Collection<String>>();
        double duration = profiler.runProfiled(Level.INFO, "Done updating necessary categories", () -> {
            final var categoryMap = categoryUtils.getCategoryMap();
            transaction.runTransacted(() -> {
                final var categoriesToBeUpdated = getCategoriesToBeUpdated(categoryMap.keySet());
                categoriesToBeUpdatedRef.set(categoriesToBeUpdated);
                log.info("Found categories to be updated: " + categoriesToBeUpdated);
                updateCategories(categoriesToBeUpdated);
            });
        });
        final var categoriesUpdatedEvent = new CategoriesUpdatedEvent(categoriesToBeUpdatedRef.get(), duration, Instant.now());
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().getPluginManager().callEvent(categoriesUpdatedEvent));
    }

    @Override
    public void updateCategoriesIfNecessaryAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::updateCategoriesIfNecessary);
        log.info("All categories updates are now scheduled (asynchronously).");
    }

    @Override
    public Collection<String> getUpdatableCategoryNames(boolean necessaryOnly) {
        final var categoryNames = categoryUtils.getCategoryMap().keySet();
        return necessaryOnly ? getCategoriesToBeUpdated(categoryNames) : categoryNames;
    }

    /**
     * Get the category names for categories that need to be updated, based on the category update interval
     *
     * @param categoryNames the names of the categories for which to check if they need to be updated.
     * @return the filtered set of category names
     */
    private Collection<String> getCategoriesToBeUpdated(Collection<String> categoryNames) {
        final var categoryUpdateInterval = categoriesProperties.getUpdate().getInterval();
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
            double duration = profiler.runProfiled(Level.DEBUG, "Done updating category: " + categoryName, () -> {
                final var foundHeadsBySource = requestCategoryHeads(categoryName);
                if (headUtils.isEmpty(foundHeadsBySource)) {
                    log.warn("No heads found for category " + categoryName + ". Skipping this category");
                    return;
                }
                log.info("Updating category: " + categoryName);
                updateCategory(categoryName, foundHeadsBySource);
            });
            final var categoryUpdatedEvent = new CategoryUpdatedEvent(categoryName, duration, Instant.now());
            Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().getPluginManager().callEvent(categoryUpdatedEvent));
        });
    }

    /**
     * Request the heads for a given category from the categorizables sources
     *
     * @param categoryName the name of the category to request heads for
     * @return the heads for that category, grouped by source
     */
    private Map<String, List<Head>> requestCategoryHeads(String categoryName) {
        return categoryUtils.getCategoryMap()
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
     * Finally the last updated timestamp will be updated for this search term
     *
     * @param categoryName  the category name of the category to update
     * @param headsBySource the heads for that category, grouped by source
     */
    private void updateCategory(String categoryName, Map<String, List<Head>> headsBySource) {
        final var category = categoryRepository.findByOrCreateFromName(categoryName);
        headsBySource.forEach((source, heads) -> {
            final var storedHeads = headUpdater.updateHeads(heads);
            storedHeads.forEach(category::addhead);
            final var database = databaseRepository.findByOrCreateFromName(source);
            storedHeads.forEach(database::addhead);
            database.addCategory(category);
        });
        category.setLastUpdated(LocalDateTime.now());
    }

}
