package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Category;
import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.CategorySearcher;
import com.github.cc007.headsplugin.integration.database.mappers.from_entity.CategoryEntityToCategoryMapper;
import com.github.cc007.headsplugin.integration.database.mappers.from_entity.HeadEntityToHeadMapper;
import com.github.cc007.headsplugin.integration.database.repositories.CategoryRepository;
import com.github.cc007.headsplugin.integration.database.transaction.Transaction;

import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CategorySearcherImpl implements CategorySearcher {

    private static final String CATEGORY_NOT_FOUND_MESSAGE
            = "Unknown category specified. Use getCategories() to find possible categories.";

    private final CategoryRepository categoryRepository;
    private final CategoryEntityToCategoryMapper categoryEntityToCategoryMapper;
    private final HeadEntityToHeadMapper headEntityToHeadMapper;
    private final Transaction transaction;

    @Override
    public Set<Category> getCategories() {
        return transaction.runTransacted(() ->
                categoryRepository.findAll()
                        .stream()
                        .map(categoryEntityToCategoryMapper::transform)
                        .collect(Collectors.toSet())
        );
    }

    @Override
    public Set<Head> getCategoryHeads(String categoryName) {
        return transaction.runTransacted(() ->
                categoryRepository.findByName(categoryName)
                        .orElseThrow(() -> new IllegalArgumentException(CATEGORY_NOT_FOUND_MESSAGE))
                        .getHeads()
                        .stream()
                        .map(headEntityToHeadMapper::transform)
                        .collect(Collectors.toSet())
        );
    }
}
