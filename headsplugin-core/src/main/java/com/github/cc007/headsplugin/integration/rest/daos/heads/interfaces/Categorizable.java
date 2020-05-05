package com.github.cc007.headsplugin.integration.rest.daos.heads.interfaces;

import com.github.cc007.headsplugin.api.business.domain.Head;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface Categorizable extends DatabaseClientDao {

    List<Head> getCategoryHeads(String categoryName);

    List<String> getPredefinedCategoryNames();

    List<String> getCustomCategoryNames();

    default List<String> getCategoryNames() {
        List<String> categories = new ArrayList<>(getPredefinedCategoryNames());
        categories.addAll(getCustomCategoryNames());
        return categories;
    }

    default List<Head> getAllCategoryHeads() {
        return getCategoryNames().stream()
                .flatMap(categoryName -> getCategoryHeads(categoryName).stream())
                .collect(Collectors.toList());
    }
}
