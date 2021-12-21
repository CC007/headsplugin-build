package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Category;
import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.mappers.from_entity.CategoryEntityToCategoryMapper;
import com.github.cc007.headsplugin.integration.database.mappers.from_entity.HeadEntityToHeadMapper;
import com.github.cc007.headsplugin.integration.database.repositories.CategoryRepository;
import com.github.cc007.headsplugin.integration.database.transaction.Transaction;

import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategorySearcherImplTest {

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    CategoryEntityToCategoryMapper categoryEntityToCategoryMapper;

    @Mock
    HeadEntityToHeadMapper headEntityToHeadMapper;

    @Mock
    Transaction transaction;

    @InjectMocks
    CategorySearcherImpl categorySearcher;

    @Test
    void getCategories() {
        // prepare
        val testCategoryEntity1 = mock(CategoryEntity.class);
        val testCategoryEntity2 = mock(CategoryEntity.class);
        val testCategory1 = Category.builder().name("Category1").build();
        val testCategory2 = Category.builder().name("Category2").build();

        when(transaction.runTransacted(isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<Set<Category>> supplier = invocation.getArgument(0);
                    return supplier.get();
                });

        when(categoryRepository.findAll())
                .thenReturn(Arrays.asList(testCategoryEntity1, testCategoryEntity2));

        when(categoryEntityToCategoryMapper.transform(testCategoryEntity1))
                .thenReturn(testCategory1);
        when(categoryEntityToCategoryMapper.transform(testCategoryEntity2))
                .thenReturn(testCategory2);

        // execute
        val actual = categorySearcher.getCategories();

        // verify
        assertThat(actual, containsInAnyOrder(testCategory1, testCategory2));
        verifyNoMoreInteractions(testCategoryEntity1, testCategoryEntity2);
        verifyNoMoreInteractions(transaction, categoryRepository, categoryEntityToCategoryMapper);
    }

    @Test
    void getCategoryHeads() {
        // prepare
        val testCategoryName = "CategoryName";
        val testCategoryEntity = mock(CategoryEntity.class);
        val testHeadEntity1 = mock(HeadEntity.class);
        val testHeadEntity2 = mock(HeadEntity.class);
        val testHead1 = Head.builder().name("Head1").build();
        val testHead2 = Head.builder().name("Head2").build();

        when(transaction.runTransacted(isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<Set<Category>> supplier = invocation.getArgument(0);
                    return supplier.get();
                });

        when(categoryRepository.findByName(testCategoryName))
                .thenReturn(Optional.of(testCategoryEntity));

        when(testCategoryEntity.getHeads())
                .thenReturn(new HashSet<>(Arrays.asList(testHeadEntity1, testHeadEntity2)));

        when(headEntityToHeadMapper.transform(testHeadEntity1))
                .thenReturn(testHead1);
        when(headEntityToHeadMapper.transform(testHeadEntity2))
                .thenReturn(testHead2);

        // execute
        val actual = categorySearcher.getCategoryHeads(testCategoryName);

        // verify
        assertThat(actual, containsInAnyOrder(testHead1, testHead2));
        verifyNoMoreInteractions(testCategoryEntity, testHeadEntity1, testHeadEntity2);
        verifyNoMoreInteractions(transaction, categoryRepository, categoryEntityToCategoryMapper);
    }

    @Test
    void getCategoryHeadsCategoryNotFound() {
        // prepare
        val testCategoryName = "CategoryName";

        when(transaction.runTransacted(isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<Set<Category>> supplier = invocation.getArgument(0);
                    return supplier.get();
                });

        when(categoryRepository.findByName(testCategoryName))
                .thenReturn(Optional.empty());

        // execute
        val actualException = Assertions.assertThrows(IllegalArgumentException.class, () ->
                categorySearcher.getCategoryHeads(testCategoryName)
        );

        // verify
        assertThat(actualException.getMessage(), both(
                containsString("Unknown category")).and(
                containsString("Use getCategories")
        ));
        verifyNoMoreInteractions(transaction, categoryRepository, categoryEntityToCategoryMapper);
    }
}