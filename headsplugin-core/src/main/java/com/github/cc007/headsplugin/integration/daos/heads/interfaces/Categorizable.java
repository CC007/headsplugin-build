package com.github.cc007.headsplugin.integration.daos.heads.interfaces;

import com.github.cc007.headsplugin.api.business.domain.Category;
import com.github.cc007.headsplugin.api.business.domain.CustomCategory;
import com.github.cc007.headsplugin.api.business.domain.Head;

import lombok.NonNull;
import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface Categorizable<C extends Category> {

    List<Head> getCategoryHeads(@NonNull C category);

    List<C> getCategories();

    String getSource();

    /**
     * Get the heads of a certain category
     * @param categoryName
     * @return the heads for the given category or {@link Optional#empty()} if the category doesn't exist.
     */
    default Optional<C> getCategory(@NonNull String categoryName) {
        return getCategories().stream()
                .filter(category -> categoryName.equalsIgnoreCase(category.getName()))
                .findAny();
    }

    default List<Head> getAllCategoryHeads() {
        return getCategories().stream()
                .flatMap(category -> getCategoryHeads(category).stream())
                .collect(Collectors.toList());
    }
}
