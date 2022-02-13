package com.github.cc007.headsplugin.integration.daos;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.HeadSearcher;
import com.github.cc007.headsplugin.config.properties.CategoriesProperties;
import com.github.cc007.headsplugin.integration.daos.interfaces.Categorizable;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class CustomCategoriesDao implements Categorizable {

    private final CategoriesProperties categoriesProperties;

    private final HeadSearcher headSearcher;

    @Override
    public List<Head> getCategoryHeads(@NonNull String categoryName) {
        val categoryHeads = new ArrayList<Head>();
        for (String searchTerm : getSearchTerms(categoryName)) {
            if (searchTerm.startsWith("uuid:")) {
                UUID uuid = UUID.fromString(searchTerm.substring(5));
                headSearcher.getHead(uuid)
                        .ifPresent(categoryHeads::add);
            } else {
                categoryHeads.addAll(
                        headSearcher.getHeads(searchTerm));
            }
        }
        return categoryHeads;
    }

    @Override
    public List<String> getCategoryNames() {
        return categoriesProperties.getCustom()
                .stream()
                .map(CategoriesProperties.CustomCategoryProperties::getName)
                .collect(Collectors.toList());
    }

    @Override
    public String getSource() {
        return "custom";
    }

    @Override
    public List<Head> getAllCategoryHeads() {
        return Categorizable.super.getAllCategoryHeads();
    }

    private List<String> getSearchTerms(String categoryName) {
        return categoriesProperties.getCustom().stream()
                .filter(custom -> categoryName.equals(custom.getName()))
                .limit(1)
                .flatMap(custom -> custom.getSearchTerms().stream())
                .collect(Collectors.toList());
    }
}
