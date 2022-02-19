package com.github.cc007.headsplugin.integration.daos.interfaces;

import com.github.cc007.headsplugin.api.business.domain.Head;

import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public interface Categorizable {

    List<Head> getCategoryHeads(@NonNull String categoryName);

    List<String> getCategoryNames();

    String getSource();

    default List<Head> getAllCategoryHeads() {
        return getCategoryNames().stream()
                .flatMap(categoryName -> getCategoryHeads(categoryName).stream())
                .distinct()
                .collect(Collectors.toList());
    }
}
