package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.business.domain.Head;
import com.github.cc007.headsplugin.business.services.chat.PrettyPrinter;
import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;
import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.mappers.to_entity.CategoryNameToCategoryEntityMapper;
import com.github.cc007.headsplugin.integration.database.mappers.to_entity.DatabaseNameToDatabaseEntityMapper;
import com.github.cc007.headsplugin.integration.database.mappers.to_entity.HeadToHeadEntityMapper;
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
public class CategoryUpdater {

    private final List<Categorizable> categorizables;
    private final HeadUpdater headUpdater;
    private final HeadUtils headUtils;
    private final CategoryRepository categoryRepository;
    private final DatabaseRepository databaseRepository;
    private final HeadRepository headRepository;
    private final CategoryNameToCategoryEntityMapper categoryNameToCategoryEntityMapper;
    private final DatabaseNameToDatabaseEntityMapper databaseNameToDatabaseEntityMapper;
    private final HeadToHeadEntityMapper headToHeadEntityMapper;
    private final PrettyPrinter prettyPrinter;

    @Value("${headsplugin.update.interval:24}")
    private int updateInterval;


    @Transactional
    public void updateCategories() {
        updateCategories(getCategoryMap());
    }

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
        val categoriesToBeUpdated = new HashMap<CategoryEntity, List<Categorizable>>();
        categories.entrySet()
                .stream()
                .filter(categoryEntry -> categoryEntry.getKey()
                        .getLastUpdated()
                        .plusHours(updateInterval)
                        .isBefore(LocalDateTime.now()))
                .forEach(categoryEntry -> categoriesToBeUpdated.put(categoryEntry.getKey(), categoryEntry.getValue()));

        updateCategories(categoriesToBeUpdated);
    }

    private void updateCategories(Map<CategoryEntity, List<Categorizable>> categories) {
        categories.forEach((key, categorizables) -> {
            String categoryName = key.getName();
            log.info("Updating category:" + categoryName);
            updateCategory(categoryName, categorizables);
            //new Thread(() -> updateCategory(categoryName, categorizables)).start();
        });
    }

    private /*synchronized*/ void updateCategory(String categoryName, List<Categorizable> categorizables) {
        val foundHeads = requestCategoryHeads(categorizables, categoryName);
        val foundHeadOwnerStrings = getHeadOwnerStrings(foundHeads);

        headUpdater.updateHeads(headUtils.flattenHeads(foundHeads.values()));
        updateCategoryHeads(categoryName, headUtils.flattenHeads(foundHeads.values()));

        for (Categorizable categorizable : categorizables) {
            val database = databaseNameToDatabaseEntityMapper.transform(categorizable.getDatabaseName());

            val foundHeadsForDatabase = foundHeads.getOrDefault(categorizable, new ArrayList<>());

            headUpdater.updateDatabaseHeads(foundHeadsForDatabase, database);
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

    private void updateCategoryHeads(String categoryName, List<Head> foundHeads) {
        val foundHeadOwnerStrings = headUtils.getHeadOwnerStrings(foundHeads);
        val categoryHeadOwnerStrings = getCategoryHeadOwnerStringsFromFound(
                categoryName,
                foundHeadOwnerStrings
        );

        val category = categoryNameToCategoryEntityMapper.transform(categoryName);
        foundHeads.stream()
                .filter(head -> !categoryHeadOwnerStrings.contains(head.getHeadOwner().toString()))
                .map(headToHeadEntityMapper::transform)
                .forEach(category::addhead);
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
