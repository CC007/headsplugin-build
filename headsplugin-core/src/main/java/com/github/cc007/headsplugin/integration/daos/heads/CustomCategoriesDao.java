package com.github.cc007.headsplugin.integration.daos.heads;

import com.github.cc007.headsplugin.api.business.domain.CustomCategory;
import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.HeadSearcher;
import com.github.cc007.headsplugin.config.properties.CategoriesProperties;
import com.github.cc007.headsplugin.integration.daos.heads.interfaces.Categorizable;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class CustomCategoriesDao implements Categorizable<CustomCategory> {

    private final CategoriesProperties categoriesProperties;

    private final HeadSearcher headSearcher;

    @Override
    public List<Head> getCategoryHeads(@NonNull CustomCategory category) {
        val categoryHeads = new ArrayList<Head>();
        for (String searchTerm : category.getSearchTerms()) {
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
    public List<CustomCategory> getCategories() {
        val customCategoriesProperties = categoriesProperties.getCustom();
        return customCategoriesProperties.stream()
                .map(customCategoryProperties -> CustomCategory.builder()
                        .name(customCategoryProperties.getName())
                        .searchTerms(customCategoryProperties.getSearchTerms())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public String getSource() {
        return "custom";
    }
}
