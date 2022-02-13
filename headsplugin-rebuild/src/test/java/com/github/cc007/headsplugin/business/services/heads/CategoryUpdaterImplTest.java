package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.Profiler;
import com.github.cc007.headsplugin.api.business.services.heads.HeadUpdater;
import com.github.cc007.headsplugin.api.business.services.heads.utils.CategoryUtils;
import com.github.cc007.headsplugin.api.business.services.heads.utils.HeadUtils;
import com.github.cc007.headsplugin.business.services.heads.utils.HeadUtilsImpl;
import com.github.cc007.headsplugin.config.properties.CategoriesProperties;
import com.github.cc007.headsplugin.integration.daos.interfaces.Categorizable;
import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;
import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.repositories.CategoryRepository;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;
import com.github.cc007.headsplugin.integration.database.transaction.Transaction;

import lombok.val;
import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryUpdaterImplTest {

    @Mock
    HeadUpdater headUpdater;

    @Mock
    CategoryUtils categoryUtils;

    @Spy
    HeadUtils headUtils = new HeadUtilsImpl();

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    DatabaseRepository databaseRepository;

    @Mock
    CategoriesProperties categoriesProperties;

    @Mock
    Transaction transaction;

    @Mock
    Profiler profiler;

    @InjectMocks
    CategoryUpdaterImpl categoryUpdater;

    @Test
    void updateCategory() {
        // prepare
        val testCategoryName1 = "CategoryName1";
        val testCategoryName2 = "CategoryName2";
        val testCategorizable11 = Mockito.mock(Categorizable.class);
        val testCategorizable12 = Mockito.mock(Categorizable.class);
        val testCategorizable21 = Mockito.mock(Categorizable.class);
        val testSource1 = "Source1";
        val testSource2 = "Source2";
        val testHead1 = Head.builder().name("Head1").build();
        val testHead2 = Head.builder().name("Head2").build();
        val testHeads11 = List.of(testHead1, testHead2);
        val testHeads12 = List.of(testHead1);
        val testCategoryEntity = Mockito.mock(CategoryEntity.class);
        val testHeadEntity1 = Mockito.mock(HeadEntity.class);
        val testHeadEntity2 = Mockito.mock(HeadEntity.class);
        val testDatabaseEntity1 = Mockito.mock(DatabaseEntity.class);
        val testDatabaseEntity2 = Mockito.mock(DatabaseEntity.class);

        val categoryMap = Map.ofEntries(
                Map.entry(testCategoryName1, Set.of(testCategorizable11, testCategorizable12)),
                Map.entry(testCategoryName2, Set.of(testCategorizable21))
        );

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(transaction).runTransacted(isA(Runnable.class));

        when(categoryUtils.getCategoryMap())
                .thenReturn(categoryMap);

        when(testCategorizable11.getSource())
                .thenReturn(testSource1);
        when(testCategorizable12.getSource())
                .thenReturn(testSource2);
        when(testCategorizable11.getCategoryHeads(testCategoryName1))
                .thenReturn(testHeads11);
        when(testCategorizable12.getCategoryHeads(testCategoryName1))
                .thenReturn(testHeads12);

        when(categoryRepository.findByOrCreateFromName(testCategoryName1))
                .thenReturn(testCategoryEntity);

        when(headUpdater.updateHeads(testHeads11))
                .thenReturn(List.of(testHeadEntity1, testHeadEntity2));
        when(headUpdater.updateHeads(testHeads12))
                .thenReturn(List.of(testHeadEntity1));

        when(databaseRepository.findByOrCreateFromName(testSource1))
                .thenReturn(testDatabaseEntity1);
        when(databaseRepository.findByOrCreateFromName(testSource2))
                .thenReturn(testDatabaseEntity2);

        // execute
        categoryUpdater.updateCategory(testCategoryName1);

        // verify
        verify(testCategoryEntity, times(2)).addhead(testHeadEntity1);
        verify(testCategoryEntity).addhead(testHeadEntity2);
        verify(testCategoryEntity).setLastUpdated(any());

        verify(testDatabaseEntity1).addhead(testHeadEntity1);
        verify(testDatabaseEntity1).addhead(testHeadEntity2);

        verify(testDatabaseEntity2).addhead(testHeadEntity1);

        verify(testDatabaseEntity1).addCategory(testCategoryEntity);
        verify(testDatabaseEntity2).addCategory(testCategoryEntity);

        verifyNoMoreInteractions(testCategorizable11, testCategorizable12, testCategorizable21);
        verifyNoMoreInteractions(testHeadEntity1, testHeadEntity2);
        verifyNoMoreInteractions(testCategoryEntity, testDatabaseEntity1, testDatabaseEntity2);
        verifyNoMoreInteractions(transaction, categoryUtils, headUpdater, categoryRepository, databaseRepository);
    }

    @Test
    void updateCategoryNoHeadsFound() {
        // prepare
        val testCategoryName1 = "CategoryName1";
        val testCategoryName2 = "CategoryName2";
        val testCategorizable11 = Mockito.mock(Categorizable.class);
        val testCategorizable12 = Mockito.mock(Categorizable.class);
        val testCategorizable21 = Mockito.mock(Categorizable.class);
        val testSource1 = "Source1";
        val testSource2 = "Source2";
        val testHead1 = Head.builder().name("Head1").build();
        val testHead2 = Head.builder().name("Head2").build();

        val categoryMap = Map.ofEntries(
                Map.entry(testCategoryName1, Set.of(testCategorizable11, testCategorizable12)),
                Map.entry(testCategoryName2, Set.of(testCategorizable21))
        );

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(transaction).runTransacted(isA(Runnable.class));

        when(categoryUtils.getCategoryMap())
                .thenReturn(categoryMap);

        when(testCategorizable11.getSource())
                .thenReturn(testSource1);
        when(testCategorizable12.getSource())
                .thenReturn(testSource2);
        when(testCategorizable11.getCategoryHeads(testCategoryName1))
                .thenReturn(Collections.emptyList());
        when(testCategorizable12.getCategoryHeads(testCategoryName1))
                .thenReturn(Collections.emptyList());


        // execute
        categoryUpdater.updateCategory(testCategoryName1);

        // verify
        verifyNoMoreInteractions(testCategorizable11, testCategorizable12, testCategorizable21);
        verifyNoMoreInteractions(transaction, categoryUtils, headUpdater, categoryRepository, databaseRepository);
    }

    @Test
    void updateCategoryNoCategorizablesFound() {
        // prepare
        val testCategoryName1 = "CategoryName1";
        val testCategoryName2 = "CategoryName2";

        val categoryMap = Map.ofEntries(
                Map.entry(testCategoryName1, Collections.<Categorizable>emptySet()),
                Map.entry(testCategoryName2, Collections.<Categorizable>emptySet())
        );

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(transaction).runTransacted(isA(Runnable.class));

        when(categoryUtils.getCategoryMap())
                .thenReturn(categoryMap);

        // execute
        categoryUpdater.updateCategory(testCategoryName1);

        // verify
        verifyNoMoreInteractions(transaction, categoryUtils, headUpdater, categoryRepository, databaseRepository);
    }

    @Test
    void updateCategories() {
        // prepare
        val testCategoryName1 = "CategoryName1";
        val testCategoryName2 = "CategoryName2";
        val testCategorizable11 = Mockito.mock(Categorizable.class);
        val testCategorizable12 = Mockito.mock(Categorizable.class);
        val testCategorizable21 = Mockito.mock(Categorizable.class);
        val testSource11 = "Source11";
        val testSource12 = "Source12";
        val testSource21 = "Source21";
        val testHead1 = Head.builder().name("Head1").build();
        val testHead2 = Head.builder().name("Head2").build();
        val testHeads11 = List.of(testHead1, testHead2);
        val testHeads12 = List.of(testHead1);
        val testHeads21 = List.of(testHead2);
        val testCategoryEntity1 = Mockito.mock(CategoryEntity.class);
        val testCategoryEntity2 = Mockito.mock(CategoryEntity.class);
        val testHeadEntity1 = Mockito.mock(HeadEntity.class);
        val testHeadEntity2 = Mockito.mock(HeadEntity.class);
        val testDatabaseEntity11 = Mockito.mock(DatabaseEntity.class);
        val testDatabaseEntity12 = Mockito.mock(DatabaseEntity.class);
        val testDatabaseEntity21 = Mockito.mock(DatabaseEntity.class);

        val categoryMap = Map.ofEntries(
                Map.entry(testCategoryName1, Set.of(testCategorizable11, testCategorizable12)),
                Map.entry(testCategoryName2, Set.of(testCategorizable21))
        );

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(2);
            runnable.run();
            return null;
        }).when(profiler).runProfiled(eq(Level.INFO), isA(String.class), isA(Runnable.class));

        //noinspection unchecked
        when(categoryUtils.getCategoryMap())
                .thenReturn(categoryMap, categoryMap);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(transaction).runTransacted(isA(Runnable.class));

        when(testCategorizable11.getSource())
                .thenReturn(testSource11);
        when(testCategorizable12.getSource())
                .thenReturn(testSource12);
        when(testCategorizable21.getSource())
                .thenReturn(testSource21);
        when(testCategorizable11.getCategoryHeads(testCategoryName1))
                .thenReturn(testHeads11);
        when(testCategorizable12.getCategoryHeads(testCategoryName1))
                .thenReturn(testHeads12);
        when(testCategorizable21.getCategoryHeads(testCategoryName2))
                .thenReturn(testHeads21);

        when(categoryRepository.findByOrCreateFromName(testCategoryName1))
                .thenReturn(testCategoryEntity1);
        when(categoryRepository.findByOrCreateFromName(testCategoryName2))
                .thenReturn(testCategoryEntity2);

        when(headUpdater.updateHeads(testHeads11))
                .thenReturn(List.of(testHeadEntity1, testHeadEntity2));
        when(headUpdater.updateHeads(testHeads12))
                .thenReturn(List.of(testHeadEntity1));
        when(headUpdater.updateHeads(testHeads21))
                .thenReturn(List.of(testHeadEntity2));

        when(databaseRepository.findByOrCreateFromName(testSource11))
                .thenReturn(testDatabaseEntity11);
        when(databaseRepository.findByOrCreateFromName(testSource12))
                .thenReturn(testDatabaseEntity12);
        when(databaseRepository.findByOrCreateFromName(testSource21))
                .thenReturn(testDatabaseEntity21);

        // execute
        categoryUpdater.updateCategories();

        // verify
        verify(testCategoryEntity1, times(2)).addhead(testHeadEntity1);
        verify(testCategoryEntity1).addhead(testHeadEntity2);
        verify(testCategoryEntity1).setLastUpdated(any());

        verify(testCategoryEntity2).addhead(testHeadEntity2);
        verify(testCategoryEntity2).setLastUpdated(any());

        verify(testDatabaseEntity11).addhead(testHeadEntity1);
        verify(testDatabaseEntity11).addhead(testHeadEntity2);

        verify(testDatabaseEntity12).addhead(testHeadEntity1);

        verify(testDatabaseEntity21).addhead(testHeadEntity2);

        verify(testDatabaseEntity11).addCategory(testCategoryEntity1);
        verify(testDatabaseEntity12).addCategory(testCategoryEntity1);
        verify(testDatabaseEntity21).addCategory(testCategoryEntity2);

        verifyNoMoreInteractions(testCategorizable11, testCategorizable12, testCategorizable21);
        verifyNoMoreInteractions(testHeadEntity1, testHeadEntity2);
        verifyNoMoreInteractions(testCategoryEntity1, testCategoryEntity2);
        verifyNoMoreInteractions(testDatabaseEntity11, testDatabaseEntity12, testDatabaseEntity21);
        verifyNoMoreInteractions(transaction, categoryUtils, headUpdater, categoryRepository, databaseRepository);
    }

    @Test
    void updateCategoriesNoHeadsFound() {
        // prepare
        val testCategoryName1 = "CategoryName1";
        val testCategoryName2 = "CategoryName2";
        val testCategorizable11 = Mockito.mock(Categorizable.class);
        val testCategorizable12 = Mockito.mock(Categorizable.class);
        val testCategorizable21 = Mockito.mock(Categorizable.class);
        val testSource11 = "Source11";
        val testSource12 = "Source12";
        val testSource21 = "Source21";
        val testHead1 = Head.builder().name("Head1").build();
        val testHead2 = Head.builder().name("Head2").build();

        val categoryMap = Map.ofEntries(
                Map.entry(testCategoryName1, Set.of(testCategorizable11, testCategorizable12)),
                Map.entry(testCategoryName2, Set.of(testCategorizable21))
        );

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(2);
            runnable.run();
            return null;
        }).when(profiler).runProfiled(eq(Level.INFO), isA(String.class), isA(Runnable.class));

        //noinspection unchecked
        when(categoryUtils.getCategoryMap())
                .thenReturn(categoryMap, categoryMap);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(transaction).runTransacted(isA(Runnable.class));

        when(testCategorizable11.getSource())
                .thenReturn(testSource11);
        when(testCategorizable12.getSource())
                .thenReturn(testSource12);
        when(testCategorizable21.getSource())
                .thenReturn(testSource21);
        when(testCategorizable11.getCategoryHeads(testCategoryName1))
                .thenReturn(Collections.emptyList());
        when(testCategorizable12.getCategoryHeads(testCategoryName1))
                .thenReturn(Collections.emptyList());
        when(testCategorizable21.getCategoryHeads(testCategoryName2))
                .thenReturn(Collections.emptyList());

        // execute
        categoryUpdater.updateCategories();

        // verify
        verifyNoMoreInteractions(testCategorizable11, testCategorizable12, testCategorizable21);
        verifyNoMoreInteractions(transaction, categoryUtils, headUpdater, categoryRepository, databaseRepository);
    }

    @Test
    void updateCategoriesNoCategorizablesFound() {
        // prepare
        val testCategoryName1 = "CategoryName1";
        val testCategoryName2 = "CategoryName2";

        val categoryMap = Map.ofEntries(
                Map.entry(testCategoryName1, Collections.<Categorizable>emptySet()),
                Map.entry(testCategoryName2, Collections.<Categorizable>emptySet())
        );

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(2);
            runnable.run();
            return null;
        }).when(profiler).runProfiled(eq(Level.INFO), isA(String.class), isA(Runnable.class));

        //noinspection unchecked
        when(categoryUtils.getCategoryMap())
                .thenReturn(categoryMap, categoryMap);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(transaction).runTransacted(isA(Runnable.class));

        // execute
        categoryUpdater.updateCategories();

        // verify
        verifyNoMoreInteractions(transaction, categoryUtils, headUpdater, categoryRepository, databaseRepository);
    }

    @Test
    void updateCategoriesIfNecessary() {
        // prepare
        val testCategoryName1 = "CategoryName1";
        val testCategoryName2 = "CategoryName2";
        val testCategorizable11 = Mockito.mock(Categorizable.class);
        val testCategorizable12 = Mockito.mock(Categorizable.class);
        val testCategorizable21 = Mockito.mock(Categorizable.class);
        val testSource1 = "Source1";
        val testSource2 = "Source2";
        val testHead1 = Head.builder().name("Head1").build();
        val testHead2 = Head.builder().name("Head2").build();
        val testHeads11 = List.of(testHead1, testHead2);
        val testHeads12 = List.of(testHead1);
        val testCategoryEntity1 = Mockito.mock(CategoryEntity.class);
        val testCategoryEntity2 = Mockito.mock(CategoryEntity.class);
        val testHeadEntity1 = Mockito.mock(HeadEntity.class);
        val testHeadEntity2 = Mockito.mock(HeadEntity.class);
        val testDatabaseEntity1 = Mockito.mock(DatabaseEntity.class);
        val testDatabaseEntity2 = Mockito.mock(DatabaseEntity.class);
        val testLastUpdated1 = LocalDateTime.now().minusHours(6);
        val testLastUpdated2 = LocalDateTime.now().minusHours(2);

        val categoryMap = Map.ofEntries(
                Map.entry(testCategoryName1, Set.of(testCategorizable11, testCategorizable12)),
                Map.entry(testCategoryName2, Set.of(testCategorizable21))
        );

        val categoryUpdateProperties = new CategoriesProperties.Update();
        categoryUpdateProperties.setInterval(4);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(2);
            runnable.run();
            return null;
        }).when(profiler).runProfiled(eq(Level.INFO), isA(String.class), isA(Runnable.class));

        //noinspection unchecked
        when(categoryUtils.getCategoryMap())
                .thenReturn(categoryMap, categoryMap);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(transaction).runTransacted(isA(Runnable.class));

        when(categoriesProperties.getUpdate())
                .thenReturn(categoryUpdateProperties);

        when(profiler.runProfiled(isA(String.class), isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(1);
                    return supplier.get();
                });

        when(categoryRepository.findByOrCreateFromName(testCategoryName1))
                .thenReturn(testCategoryEntity1, testCategoryEntity1);
        when(categoryRepository.findByOrCreateFromName(testCategoryName2))
                .thenReturn(testCategoryEntity2);

        when(testCategoryEntity1.getLastUpdated())
                .thenReturn(testLastUpdated1);
        when(testCategoryEntity2.getLastUpdated())
                .thenReturn(testLastUpdated2);

        when(testCategorizable11.getSource())
                .thenReturn(testSource1);
        when(testCategorizable12.getSource())
                .thenReturn(testSource2);
        when(testCategorizable11.getCategoryHeads(testCategoryName1))
                .thenReturn(testHeads11);
        when(testCategorizable12.getCategoryHeads(testCategoryName1))
                .thenReturn(testHeads12);

        when(headUpdater.updateHeads(testHeads11))
                .thenReturn(List.of(testHeadEntity1, testHeadEntity2));
        when(headUpdater.updateHeads(testHeads12))
                .thenReturn(List.of(testHeadEntity1));

        when(databaseRepository.findByOrCreateFromName(testSource1))
                .thenReturn(testDatabaseEntity1);
        when(databaseRepository.findByOrCreateFromName(testSource2))
                .thenReturn(testDatabaseEntity2);

        // execute
        categoryUpdater.updateCategoriesIfNecessary();

        // verify
        verify(testCategoryEntity1, times(2)).addhead(testHeadEntity1);
        verify(testCategoryEntity1).addhead(testHeadEntity2);
        verify(testCategoryEntity1).setLastUpdated(any());

        verify(testDatabaseEntity1).addhead(testHeadEntity1);
        verify(testDatabaseEntity1).addhead(testHeadEntity2);

        verify(testDatabaseEntity2).addhead(testHeadEntity1);

        verify(testDatabaseEntity1).addCategory(testCategoryEntity1);
        verify(testDatabaseEntity2).addCategory(testCategoryEntity1);

        verifyNoMoreInteractions(testCategorizable11, testCategorizable12, testCategorizable21);
        verifyNoMoreInteractions(testHeadEntity1, testHeadEntity2);
        verifyNoMoreInteractions(testCategoryEntity1, testCategoryEntity2);
        verifyNoMoreInteractions(testDatabaseEntity1, testDatabaseEntity2);
        verifyNoMoreInteractions(
                transaction,
                categoryUtils,
                headUpdater,
                categoryRepository,
                databaseRepository,
                categoriesProperties
        );
    }

    @Test
    void updateCategoriesIfNecessaryNoHeadsFound() {
        // prepare
        val testCategoryName1 = "CategoryName1";
        val testCategoryName2 = "CategoryName2";
        val testCategorizable11 = Mockito.mock(Categorizable.class);
        val testCategorizable12 = Mockito.mock(Categorizable.class);
        val testCategorizable21 = Mockito.mock(Categorizable.class);
        val testSource1 = "Source1";
        val testSource2 = "Source2";
        val testHead1 = Head.builder().name("Head1").build();
        val testHead2 = Head.builder().name("Head2").build();
        val testCategoryEntity1 = Mockito.mock(CategoryEntity.class);
        val testCategoryEntity2 = Mockito.mock(CategoryEntity.class);
        val testLastUpdated1 = LocalDateTime.now().minusHours(6);
        val testLastUpdated2 = LocalDateTime.now().minusHours(2);

        val categoryMap = Map.ofEntries(
                Map.entry(testCategoryName1, Set.of(testCategorizable11, testCategorizable12)),
                Map.entry(testCategoryName2, Set.of(testCategorizable21))
        );

        val categoryUpdateProperties = new CategoriesProperties.Update();
        categoryUpdateProperties.setInterval(4);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(2);
            runnable.run();
            return null;
        }).when(profiler).runProfiled(eq(Level.INFO), isA(String.class), isA(Runnable.class));

        //noinspection unchecked
        when(categoryUtils.getCategoryMap())
                .thenReturn(categoryMap, categoryMap);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(transaction).runTransacted(isA(Runnable.class));

        when(categoriesProperties.getUpdate())
                .thenReturn(categoryUpdateProperties);

        when(profiler.runProfiled(isA(String.class), isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(1);
                    return supplier.get();
                });

        when(categoryRepository.findByOrCreateFromName(testCategoryName1))
                .thenReturn(testCategoryEntity1, testCategoryEntity1);
        when(categoryRepository.findByOrCreateFromName(testCategoryName2))
                .thenReturn(testCategoryEntity2);

        when(testCategoryEntity1.getLastUpdated())
                .thenReturn(testLastUpdated1);
        when(testCategoryEntity2.getLastUpdated())
                .thenReturn(testLastUpdated2);

        when(testCategorizable11.getSource())
                .thenReturn(testSource1);
        when(testCategorizable12.getSource())
                .thenReturn(testSource2);
        when(testCategorizable11.getCategoryHeads(testCategoryName1))
                .thenReturn(Collections.emptyList());
        when(testCategorizable12.getCategoryHeads(testCategoryName1))
                .thenReturn(Collections.emptyList());


        // execute
        categoryUpdater.updateCategoriesIfNecessary();

        // verify
        verifyNoMoreInteractions(testCategorizable11, testCategorizable12, testCategorizable21);
        verifyNoMoreInteractions(testCategoryEntity1, testCategoryEntity2);
        verifyNoMoreInteractions(
                transaction,
                categoryUtils,
                headUpdater,
                categoryRepository,
                databaseRepository,
                categoriesProperties
        );
    }

    @Test
    void updateCategoriesIfNecessaryNoCategorizablesFound() {
        // prepare
        val testCategoryName1 = "CategoryName1";
        val testCategoryName2 = "CategoryName2";
        val testCategoryEntity1 = Mockito.mock(CategoryEntity.class);
        val testCategoryEntity2 = Mockito.mock(CategoryEntity.class);
        val testLastUpdated1 = LocalDateTime.now().minusHours(6);
        val testLastUpdated2 = LocalDateTime.now().minusHours(2);

        val categoryMap = Map.ofEntries(
                Map.entry(testCategoryName1, Collections.<Categorizable>emptySet()),
                Map.entry(testCategoryName2, Collections.<Categorizable>emptySet())
        );

        val categoryUpdateProperties = new CategoriesProperties.Update();
        categoryUpdateProperties.setInterval(4);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(2);
            runnable.run();
            return null;
        }).when(profiler).runProfiled(eq(Level.INFO), isA(String.class), isA(Runnable.class));

        //noinspection unchecked
        when(categoryUtils.getCategoryMap())
                .thenReturn(categoryMap, categoryMap);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(transaction).runTransacted(isA(Runnable.class));

        when(categoriesProperties.getUpdate())
                .thenReturn(categoryUpdateProperties);

        when(profiler.runProfiled(isA(String.class), isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(1);
                    return supplier.get();
                });

        when(categoryRepository.findByOrCreateFromName(testCategoryName1))
                .thenReturn(testCategoryEntity1, testCategoryEntity1);
        when(categoryRepository.findByOrCreateFromName(testCategoryName2))
                .thenReturn(testCategoryEntity2);

        when(testCategoryEntity1.getLastUpdated())
                .thenReturn(testLastUpdated1);
        when(testCategoryEntity2.getLastUpdated())
                .thenReturn(testLastUpdated2);

        // execute
        categoryUpdater.updateCategoriesIfNecessary();

        // verify
        verifyNoMoreInteractions(testCategoryEntity1, testCategoryEntity2);
        verifyNoMoreInteractions(
                transaction,
                categoryUtils,
                headUpdater,
                categoryRepository,
                databaseRepository,
                categoriesProperties
        );
    }

    @Test
    void getUpdatableCategoryNames() {
        // prepare
        val testCategoryName1 = "CategoryName1";
        val testCategoryName2 = "CategoryName2";
        val testCategorizable1 = Mockito.mock(Categorizable.class);
        val testCategorizable2 = Mockito.mock(Categorizable.class);

        val categoryMap = Map.ofEntries(
                Map.entry(testCategoryName1, Set.of(testCategorizable1)),
                Map.entry(testCategoryName2, Set.of(testCategorizable2))
        );

        when(categoryUtils.getCategoryMap())
                .thenReturn(categoryMap);

        // execute
        val actual = categoryUpdater.getUpdatableCategoryNames(false);

        // verify
        verifyNoMoreInteractions(categoryUtils);
        assertThat(actual, containsInAnyOrder(testCategoryName1, testCategoryName2));
    }

    @Test
    void getUpdatableCategoryNamesNecessaryOnly() {
        // prepare
        val testCategoryName1 = "CategoryName1";
        val testCategoryName2 = "CategoryName2";
        val testCategorizable1 = Mockito.mock(Categorizable.class);
        val testCategorizable2 = Mockito.mock(Categorizable.class);
        val testCategoryEntity1 = Mockito.mock(CategoryEntity.class);
        val testCategoryEntity2 = Mockito.mock(CategoryEntity.class);
        val testLastUpdated1 = LocalDateTime.now().minusHours(6);
        val testLastUpdated2 = LocalDateTime.now().minusHours(2);

        val categoryMap = Map.ofEntries(
                Map.entry(testCategoryName1, Set.of(testCategorizable1)),
                Map.entry(testCategoryName2, Set.of(testCategorizable2))
        );

        val categoryUpdateProperties = new CategoriesProperties.Update();
        categoryUpdateProperties.setInterval(4);

        when(categoriesProperties.getUpdate())
                .thenReturn(categoryUpdateProperties);
        when(categoryUtils.getCategoryMap())
                .thenReturn(categoryMap);

        when(profiler.runProfiled(isA(String.class), isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(1);
                    return supplier.get();
                });

        when(categoryRepository.findByOrCreateFromName(testCategoryName1))
                .thenReturn(testCategoryEntity1);
        when(categoryRepository.findByOrCreateFromName(testCategoryName2))
                .thenReturn(testCategoryEntity2);

        when(testCategoryEntity1.getLastUpdated())
                .thenReturn(testLastUpdated1);
        when(testCategoryEntity2.getLastUpdated())
                .thenReturn(testLastUpdated2);

        // execute
        val actual = categoryUpdater.getUpdatableCategoryNames(true);

        // verify
        verifyNoMoreInteractions(categoryUtils, categoryRepository, categoriesProperties);
        verifyNoMoreInteractions(testCategoryEntity1, testCategoryEntity2);
        assertThat(actual, contains(testCategoryName1));
    }
}