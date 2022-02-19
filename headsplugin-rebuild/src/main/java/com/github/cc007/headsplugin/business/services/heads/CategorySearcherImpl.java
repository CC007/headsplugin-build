package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Category;
import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.Profiler;
import com.github.cc007.headsplugin.api.business.services.heads.CategorySearcher;
import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.repositories.CategoryRepository;
import com.github.cc007.headsplugin.integration.database.transaction.Transaction;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Transformer;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CategorySearcherImpl implements CategorySearcher {

    private static final String CATEGORY_NOT_FOUND_MESSAGE
            = "Unknown category specified. Use getCategories() to find possible categories.";

    private final CategoryRepository categoryRepository;
    private final Transformer<CategoryEntity, Category> categoryEntityToCategoryMapper;
    private final Transformer<HeadEntity, Head> headEntityToHeadMapper;
    private final Transaction transaction;
    private final Profiler profiler;

    @Override
    public Set<Category> getCategories() {
        return profiler.runProfiled("Done getting categories", () ->
                transaction.runTransacted(() ->
                        categoryRepository.findAll()
                                .stream()
                                .map(categoryEntityToCategoryMapper::transform)
                                .collect(Collectors.toSet())
                )
        );
    }

    @Override
    public Set<Head> getCategoryHeads(String categoryName) {
        return profiler.runProfiled("Done getting heads from category " + categoryName, () ->
                transaction.runTransacted(() ->
                        categoryRepository.findByName(categoryName)
                                .orElseThrow(() -> new IllegalArgumentException(CATEGORY_NOT_FOUND_MESSAGE))
                                .getHeads()
                                .stream()
                                .map(headEntityToHeadMapper::transform)
                                .collect(Collectors.toSet())
                )
        );
    }
}
