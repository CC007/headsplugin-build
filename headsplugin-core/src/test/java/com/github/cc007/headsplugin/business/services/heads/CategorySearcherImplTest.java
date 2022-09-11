package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Category;
import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.Profiler;
import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.repositories.CategoryRepository;
import com.github.cc007.headsplugin.integration.database.transaction.Transaction;

import org.apache.commons.collections4.Transformer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
    Transformer<CategoryEntity, Category> categoryEntityToCategoryMapper;

    @Mock
    Transformer<HeadEntity, Head> headEntityToHeadMapper;

    @Mock
    Transaction transaction;

    @Mock
    Profiler profiler;

    CategorySearcherImpl categorySearcher;

    /**
     * Use setUp method to create the object under test,
     * because @{@link InjectMocks} doesn't inject the right Transformer into the right field.
     * It doesn't take generic type arguments into account.
     */
    @BeforeEach
    void setUp() {
        categorySearcher = new CategorySearcherImpl(categoryRepository, categoryEntityToCategoryMapper, headEntityToHeadMapper, transaction, profiler);
    }

    @Test
    void getCategories() {
        // prepare
        final var testCategoryEntity1 = mock(CategoryEntity.class);
        final var testCategoryEntity2 = mock(CategoryEntity.class);
        final var testCategory1 = Category.builder().name("Category1").build();
        final var testCategory2 = Category.builder().name("Category2").build();

        when(profiler.runProfiled(isA(String.class), isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(1);
                    return supplier.get();
                });

        when(transaction.runTransacted(isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(0);
                    return supplier.get();
                });

        when(categoryRepository.findAll())
                .thenReturn(List.of(testCategoryEntity1, testCategoryEntity2));

        when(categoryEntityToCategoryMapper.transform(testCategoryEntity1))
                .thenReturn(testCategory1);
        when(categoryEntityToCategoryMapper.transform(testCategoryEntity2))
                .thenReturn(testCategory2);

        // execute
        final var actual = categorySearcher.getCategories();

        // verify
        assertThat(actual, containsInAnyOrder(testCategory1, testCategory2));
        verifyNoMoreInteractions(testCategoryEntity1, testCategoryEntity2);
        verifyNoMoreInteractions(transaction, categoryRepository, categoryEntityToCategoryMapper);
    }

    @Test
    void getCategoryHeads() {
        // prepare
        final var testCategoryName = "CategoryName";
        final var testCategoryEntity = mock(CategoryEntity.class);
        final var testHeadEntity1 = mock(HeadEntity.class);
        final var testHeadEntity2 = mock(HeadEntity.class);
        final var testHead1 = Head.builder().name("Head1").build();
        final var testHead2 = Head.builder().name("Head2").build();

        when(profiler.runProfiled(isA(String.class), isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(1);
                    return supplier.get();
                });

        when(transaction.runTransacted(isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(0);
                    return supplier.get();
                });

        when(categoryRepository.findByName(testCategoryName))
                .thenReturn(Optional.of(testCategoryEntity));

        when(testCategoryEntity.getHeads())
                .thenReturn(Set.of(testHeadEntity1, testHeadEntity2));

        when(headEntityToHeadMapper.transform(testHeadEntity1))
                .thenReturn(testHead1);
        when(headEntityToHeadMapper.transform(testHeadEntity2))
                .thenReturn(testHead2);

        // execute
        final var actual = categorySearcher.getCategoryHeads(testCategoryName);

        // verify
        assertThat(actual, containsInAnyOrder(testHead1, testHead2));
        verifyNoMoreInteractions(testCategoryEntity, testHeadEntity1, testHeadEntity2);
        verifyNoMoreInteractions(profiler, transaction, categoryRepository, categoryEntityToCategoryMapper);
    }

    @Test
    void getCategoryHeadsCategoryNotFound() {
        // prepare
        final var testCategoryName = "CategoryName";

        when(profiler.runProfiled(isA(String.class), isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(1);
                    return supplier.get();
                });

        when(transaction.runTransacted(isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(0);
                    return supplier.get();
                });

        when(categoryRepository.findByName(testCategoryName))
                .thenReturn(Optional.empty());

        // execute
        final var actualException = Assertions.assertThrows(IllegalArgumentException.class, () ->
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