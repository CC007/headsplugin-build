package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Category;
import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.business.services.chat.PrettyPrinter;
import com.github.cc007.headsplugin.config.aspects.profiler.Profiler;
import com.github.cc007.headsplugin.integration.daos.heads.interfaces.Categorizable;
import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.mappers.to_entity.CategoryNameToCategoryEntityMapper;
import com.github.cc007.headsplugin.integration.database.mappers.to_entity.DatabaseNameToDatabaseEntityMapper;
import com.github.cc007.headsplugin.integration.database.repositories.CategoryRepository;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;
import com.github.cc007.headsplugin.integration.database.repositories.HeadRepository;

import dev.alangomes.springspigot.util.scheduler.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Log4j2
public class CategoryUpdaterImpl implements com.github.cc007.headsplugin.api.business.services.heads.CategoryUpdater {

    private List<Categorizable<Category>> categorizables;
    private final HeadUpdater headUpdater;
    private final HeadUtils headUtils;
    private final CategoryRepository categoryRepository;
    private final DatabaseRepository databaseRepository;
    private final HeadRepository headRepository;
    private final CategoryNameToCategoryEntityMapper categoryNameToCategoryEntityMapper;
    private final DatabaseNameToDatabaseEntityMapper databaseNameToDatabaseEntityMapper;
    private final PrettyPrinter prettyPrinter;

    @Value("${headsplugin.categories.update.interval:24}")
    private int updateInterval;

    @Autowired
    public void setCategorizables(List<Categorizable<? extends Category>> categorizables) {
        this.categorizables = new ArrayList<>();
        for (Categorizable<? extends Category> categorizable : categorizables) {
            this.categorizables.add((Categorizable<Category>) categorizable);
        }
    }

    @Override
    @Transactional
    public void updateCategory(String categoryName) throws IllegalArgumentException {
        val categorizableMap = getCategoryMap(categorizables).get(categoryName);
        updateCategory(categoryName, categorizableMap);
    }

    @Override
    @Transactional
    @Profiler(message = "Done updating all categories:", logLevel = Level.INFO)
    public void updateCategories() {
        updateCategories(getCategoryMap(categorizables));
    }

    @Override
    @Transactional
    @Profiler(message = "Done updating necessary categories:", logLevel = Level.INFO)
    public void updateCategoriesIfNecessary() {
        val categoriesToBeUpdated = getCategoriesToBeUpdatedMap(getCategoryMap(categorizables));
        updateCategories(categoriesToBeUpdated);
    }

    @Override
    public Collection<String> getUpdatableCategoryNames(boolean necessaryOnly) {
        if(necessaryOnly) {
            return getCategoriesToBeUpdatedMap(getCategoryMap(categorizables)).keySet();
        } else {
            return getCategoryMap(categorizables).keySet();
        }
    }

    private <C extends Category> Map<String, Map<C, Categorizable<C>>> getCategoryMap(List<Categorizable<C>> categorizables) {
        val categoryMap = new HashMap<String, Map<C, Categorizable<C>>>();
        for (Categorizable<C> categorizable : categorizables) {
            for (val category : categorizable.getCategories()) {
                val headsSupplierMap = categoryMap.computeIfAbsent(category.getName(), (key) -> new HashMap<>());
                headsSupplierMap.put(category, categorizable);
            }
        }
        return categoryMap;
    }

    private <C extends Category> Map<String, Map<C, Categorizable<C>>> getCategoriesToBeUpdatedMap(Map<String, Map<C, Categorizable<C>>> categoryMap) {
        long start = System.currentTimeMillis();
        Map<String, Map<C, Categorizable<C>>> categoriesToBeUpdated = categoryMap.entrySet()
                .stream()
                .filter(categoryEntry -> categoryNameToCategoryEntityMapper
                        .transform(categoryEntry.getKey())
                        .getLastUpdated()
                        .plusHours(updateInterval)
                        .isBefore(LocalDateTime.now()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        long end = System.currentTimeMillis();
        log.debug(String.format("Done filtering categories to be updated (in %.3fs).", (end - start) / 1000.0));
        return categoriesToBeUpdated;
    }

    private <C extends Category> void updateCategories(Map<String, Map<C, Categorizable<C>>> categoryMap) {
        categoryMap.forEach((categoryName, categorizableMap) -> {
            log.info("Updating category: " + categoryName);
            updateCategory(categoryName, categorizableMap);
        });
    }

    private <C extends Category> void updateCategory(String categoryName, Map<C, Categorizable<C>> categorizableMap) {
        val foundHeads = requestCategoryHeads(categorizableMap);

        val headEntities = headUpdater.updateHeads(foundHeads);

        updateCategoryHeads(categoryName, headEntities);

        for (Categorizable<C> categorizable : categorizableMap.values()) {
            val database = databaseNameToDatabaseEntityMapper.transform(categorizable.getSource());

            headUpdater.updateDatabaseHeads(headEntities, database);
            updateDatabaseCategory(categoryName, database);
        }
    }

    private <C extends Category> List<Head> requestCategoryHeads(
            Map<C, Categorizable<C>> categorizableMap) {
        return categorizableMap.entrySet().stream()
                .flatMap(categorizableEntry -> {
                    val key = categorizableEntry.getKey();
                    val value = categorizableEntry.getValue();
                    return value.getCategoryHeads(key).stream();
                })
                .collect(Collectors.toList());
    }

    private void updateCategoryHeads(String categoryName, List<HeadEntity> foundHeadEntities) {
        val category = categoryNameToCategoryEntityMapper.transform(categoryName);

        foundHeadEntities.forEach(category::addhead);
        category.setLastUpdated(LocalDateTime.now());
        categoryRepository.save(category);
    }

    private void updateDatabaseCategory(String categoryName, DatabaseEntity database) {
        val category = categoryNameToCategoryEntityMapper.transform(categoryName);
        database.addCategory(category);
        databaseRepository.save(database);
    }
}
