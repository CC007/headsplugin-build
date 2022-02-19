package com.github.cc007.headsplugin.business.services.heads.utils;

import com.github.cc007.headsplugin.api.business.services.heads.utils.CategoryUtils;
import com.github.cc007.headsplugin.integration.daos.interfaces.Categorizable;

import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class CategoryUtilsImpl implements CategoryUtils {

    private final Set<Categorizable> categorizables;

    private Map<String, Set<Categorizable>> categoryMap;

    @Override
    public Map<String, Set<Categorizable>> getCategoryMap() {
        if (categoryMap == null) {
            categoryMap = new HashMap<>();
            for (val categorizable : categorizables) {
                for (val categoryName : categorizable.getCategoryNames()) {
                    val categoryNameCategorizables = categoryMap.computeIfAbsent(categoryName, (key) -> new HashSet<>());
                    categoryNameCategorizables.add(categorizable);
                }
            }
        }
        return categoryMap;
    }

    @Override
    public void clearCategoryMap() {
        categoryMap = null;
    }
}
