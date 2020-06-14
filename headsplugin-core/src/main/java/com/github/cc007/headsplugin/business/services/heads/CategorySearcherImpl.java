package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Category;
import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.CategorySearcher;
import com.github.cc007.headsplugin.integration.database.mappers.from_entity.CategoryEntityToCategoryMapper;
import com.github.cc007.headsplugin.integration.database.mappers.from_entity.HeadEntityToHeadMapper;
import com.github.cc007.headsplugin.integration.database.repositories.CategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class CategorySearcherImpl implements CategorySearcher {

    private final CategoryRepository categoryRepository;
    private final CategoryEntityToCategoryMapper categoryEntityToCategoryMapper;
    private final HeadEntityToHeadMapper headEntityToHeadMapper;

    @Override
    @Transactional(readOnly = true)
    public Set<Category> getCategories() {
        return StreamSupport.stream(categoryRepository.findAll().spliterator(), false)
                .map(categoryEntityToCategoryMapper::transform)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Head> getCategoryHeads(String categoryName) {
        val headEntities = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new IllegalArgumentException("Unknown category specified. Use listCategories() to find possible categories."))
                .getHeads();
        return headEntities.stream()
                .map(headEntityToHeadMapper::transform)
                .collect(Collectors.toSet());
    }
}
