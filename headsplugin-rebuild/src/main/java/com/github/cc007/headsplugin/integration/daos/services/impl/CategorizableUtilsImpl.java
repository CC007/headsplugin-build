package com.github.cc007.headsplugin.integration.daos.services.impl;

import com.github.cc007.headsplugin.integration.daos.interfaces.Categorizable;
import com.github.cc007.headsplugin.integration.daos.services.CategorizableUtils;

import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class CategorizableUtilsImpl implements CategorizableUtils {

    private final List<Categorizable> categorizables;

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
