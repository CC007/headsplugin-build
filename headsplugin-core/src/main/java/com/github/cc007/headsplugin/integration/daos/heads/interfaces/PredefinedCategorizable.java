package com.github.cc007.headsplugin.integration.daos.heads.interfaces;

import com.github.cc007.headsplugin.api.business.domain.Category;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface PredefinedCategorizable extends Categorizable<Category>, DatabaseClientDao {

    List<String> getPredefinedCategoryNames();

    @Override
    default String getSource() {
        return getDatabaseName();
    }

    @Override
    default List<Category> getCategories() {
        return getPredefinedCategoryNames().stream()
                .map(categoryName ->
                        Category.builder()
                                .name(categoryName)
                                .sources(Collections.singletonList(getDatabaseName()))
                                .build())
                .collect(Collectors.toList());
    }
}
