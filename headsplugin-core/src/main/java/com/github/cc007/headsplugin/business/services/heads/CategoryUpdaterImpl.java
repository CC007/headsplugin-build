package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.business.services.chat.PrettyPrinter;
import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;
import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.mappers.to_entity.CategoryNameToCategoryEntityMapper;
import com.github.cc007.headsplugin.integration.database.mappers.to_entity.DatabaseNameToDatabaseEntityMapper;
import com.github.cc007.headsplugin.integration.database.repositories.CategoryRepository;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;
import com.github.cc007.headsplugin.integration.database.repositories.HeadRepository;
import com.github.cc007.headsplugin.integration.rest.daos.heads.interfaces.Categorizable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryUpdaterImpl implements com.github.cc007.headsplugin.api.business.services.heads.CategoryUpdater {

    private final List<Categorizable> categorizables;
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


    @Override
    @Transactional
    public void updateCategory(String categoryName) {
        Map<CategoryEntity, List<Categorizable>> categoryMap = getCategoryMap().entrySet().stream()
                .filter(c -> categoryName.equals(c.getKey().getName()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        updateCategories(categoryMap);
    }

    @Override
    @Transactional
    public void updateCategories() {
        updateCategories(getCategoryMap());
    }

    @Override
    @Transactional
    public void updateCategoriesIfNecessary() {
        updateCategoriesIfNecessary(getCategoryMap());
    }

    private Map<CategoryEntity, List<Categorizable>> getCategoryMap() {
        val categories = new HashMap<CategoryEntity, List<Categorizable>>();
        for (val categorizable : categorizables) {
            for (String categoryName : categorizable.getCategoryNames()) {
                val category = categories.keySet()
                        .stream()
                        .filter(c -> categoryName.equals(c.getName()))
                        .findAny()
                        .orElseGet(() -> categoryNameToCategoryEntityMapper.transform(categoryName));
                categories.computeIfAbsent(category, k -> new ArrayList<>())
                        .add(categorizable);
            }
        }
        return categories;
    }

    private void updateCategoriesIfNecessary(Map<CategoryEntity, List<Categorizable>> categories) {
        long start = System.currentTimeMillis();
        val categoriesToBeUpdated = new HashMap<CategoryEntity, List<Categorizable>>();
        categories.entrySet()
                .stream()
                .filter(categoryEntry -> categoryEntry.getKey()
                        .getLastUpdated()
                        .plusHours(updateInterval)
                        .isBefore(LocalDateTime.now()))
                .forEach(categoryEntry -> categoriesToBeUpdated.put(categoryEntry.getKey(), categoryEntry.getValue()));

        updateCategories(categoriesToBeUpdated);

        long end = System.currentTimeMillis();
        log.info(String.format("Done updating all categories (in %.3fs).", (end - start) / 1000.0));
    }

    private void updateCategories(Map<CategoryEntity, List<Categorizable>> categories) {
        categories.forEach((key, categorizables) -> {
            String categoryName = key.getName();
            log.info("Updating category: " + categoryName);
            updateCategory(categoryName, categorizables);
        });
    }

    private void updateCategory(String categoryName, List<Categorizable> categorizables) {
        val foundHeads = requestCategoryHeads(categorizables, categoryName);

        val headEntities = headUpdater.updateHeads(headUtils.flattenHeads(foundHeads.values()));

        updateCategoryHeads(categoryName, headEntities);

        for (Categorizable categorizable : categorizables) {
            val database = databaseNameToDatabaseEntityMapper.transform(categorizable.getDatabaseName());

            headUpdater.updateDatabaseHeads(headEntities, database);
            updateDatabaseCategory(categoryName, database);
        }
    }

    private void updateDatabaseCategory(String categoryName, DatabaseEntity database) {
        val category = categoryNameToCategoryEntityMapper.transform(categoryName);
        database.addCategory(category);
        databaseRepository.save(database);
    }

    private Map<Categorizable, List<Head>> requestCategoryHeads(List<Categorizable> categorizables, String categoryName) {
        return categorizables
                .stream()
                .collect(Collectors.toMap(
                        categorizable -> categorizable,
                        categorizable -> categorizable.getCategoryHeads(categoryName)
                ));
    }

    private Map<Categorizable, List<String>> getHeadOwnerStrings(Map<Categorizable, List<Head>> heads) {
        return heads.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                categorizableListEntry -> headUtils.getHeadOwnerStrings(categorizableListEntry.getValue())
        ));
    }

    private void updateCategoryHeads(String categoryName, List<HeadEntity> foundHeadEntities) {
        val category = categoryNameToCategoryEntityMapper.transform(categoryName);

        foundHeadEntities.forEach(category::addhead);
        category.setLastUpdated(LocalDateTime.now());
        categoryRepository.save(category);
    }

    private List<String> getCategoryHeadOwnerStringsFromFound(String categoryName, List<String> foundHeadOwnerStrings) {
        return headRepository.findByCategories_NameAndHeadOwnerIn(categoryName, foundHeadOwnerStrings)
                .stream()
                .map(HeadEntity::getHeadOwner)
                .collect(Collectors.toList());
    }
}
