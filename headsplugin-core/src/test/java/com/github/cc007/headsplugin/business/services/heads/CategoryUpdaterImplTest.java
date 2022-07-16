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

import org.apache.logging.log4j.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
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
    Plugin plugin;

    @Mock
    CategoriesProperties categoriesProperties;

    @Mock
    Transaction transaction;

    @Mock
    Profiler profiler;

    @Captor
    ArgumentCaptor<Runnable> asyncCaptor;

    @InjectMocks
    @Spy
    CategoryUpdaterImpl categoryUpdater;

    @Test
    void updateCategory() {
        // prepare
        final var testCategoryName1 = "CategoryName1";
        final var testCategoryName2 = "CategoryName2";
        final var testCategorizable11 = Mockito.mock(Categorizable.class);
        final var testCategorizable12 = Mockito.mock(Categorizable.class);
        final var testCategorizable21 = Mockito.mock(Categorizable.class);
        final var testSource1 = "Source1";
        final var testSource2 = "Source2";
        final var testHead1 = Head.builder().name("Head1").build();
        final var testHead2 = Head.builder().name("Head2").build();
        final var testHeads11 = List.of(testHead1, testHead2);
        final var testHeads12 = List.of(testHead1);
        final var testCategoryEntity = Mockito.mock(CategoryEntity.class);
        final var testHeadEntity1 = Mockito.mock(HeadEntity.class);
        final var testHeadEntity2 = Mockito.mock(HeadEntity.class);
        final var testDatabaseEntity1 = Mockito.mock(DatabaseEntity.class);
        final var testDatabaseEntity2 = Mockito.mock(DatabaseEntity.class);

        final var categoryMap = Map.ofEntries(
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
        final var testCategoryName1 = "CategoryName1";
        final var testCategoryName2 = "CategoryName2";
        final var testCategorizable11 = Mockito.mock(Categorizable.class);
        final var testCategorizable12 = Mockito.mock(Categorizable.class);
        final var testCategorizable21 = Mockito.mock(Categorizable.class);
        final var testSource1 = "Source1";
        final var testSource2 = "Source2";
        final var testHead1 = Head.builder().name("Head1").build();
        final var testHead2 = Head.builder().name("Head2").build();

        final var categoryMap = Map.ofEntries(
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
        final var testCategoryName1 = "CategoryName1";
        final var testCategoryName2 = "CategoryName2";

        final var categoryMap = Map.ofEntries(
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
        final var testCategoryName1 = "CategoryName1";
        final var testCategoryName2 = "CategoryName2";
        final var testCategorizable11 = Mockito.mock(Categorizable.class);
        final var testCategorizable12 = Mockito.mock(Categorizable.class);
        final var testCategorizable21 = Mockito.mock(Categorizable.class);
        final var testSource11 = "Source11";
        final var testSource12 = "Source12";
        final var testSource21 = "Source21";
        final var testHead1 = Head.builder().name("Head1").build();
        final var testHead2 = Head.builder().name("Head2").build();
        final var testHeads11 = List.of(testHead1, testHead2);
        final var testHeads12 = List.of(testHead1);
        final var testHeads21 = List.of(testHead2);
        final var testCategoryEntity1 = Mockito.mock(CategoryEntity.class);
        final var testCategoryEntity2 = Mockito.mock(CategoryEntity.class);
        final var testHeadEntity1 = Mockito.mock(HeadEntity.class);
        final var testHeadEntity2 = Mockito.mock(HeadEntity.class);
        final var testDatabaseEntity11 = Mockito.mock(DatabaseEntity.class);
        final var testDatabaseEntity12 = Mockito.mock(DatabaseEntity.class);
        final var testDatabaseEntity21 = Mockito.mock(DatabaseEntity.class);

        final var categoryMap = Map.ofEntries(
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
        final var testCategoryName1 = "CategoryName1";
        final var testCategoryName2 = "CategoryName2";
        final var testCategorizable11 = Mockito.mock(Categorizable.class);
        final var testCategorizable12 = Mockito.mock(Categorizable.class);
        final var testCategorizable21 = Mockito.mock(Categorizable.class);
        final var testSource11 = "Source11";
        final var testSource12 = "Source12";
        final var testSource21 = "Source21";
        final var testHead1 = Head.builder().name("Head1").build();
        final var testHead2 = Head.builder().name("Head2").build();

        final var categoryMap = Map.ofEntries(
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
        final var testCategoryName1 = "CategoryName1";
        final var testCategoryName2 = "CategoryName2";

        final var categoryMap = Map.ofEntries(
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
        final var testCategoryName1 = "CategoryName1";
        final var testCategoryName2 = "CategoryName2";
        final var testCategorizable11 = Mockito.mock(Categorizable.class);
        final var testCategorizable12 = Mockito.mock(Categorizable.class);
        final var testCategorizable21 = Mockito.mock(Categorizable.class);
        final var testSource1 = "Source1";
        final var testSource2 = "Source2";
        final var testHead1 = Head.builder().name("Head1").build();
        final var testHead2 = Head.builder().name("Head2").build();
        final var testHeads11 = List.of(testHead1, testHead2);
        final var testHeads12 = List.of(testHead1);
        final var testCategoryEntity1 = Mockito.mock(CategoryEntity.class);
        final var testCategoryEntity2 = Mockito.mock(CategoryEntity.class);
        final var testHeadEntity1 = Mockito.mock(HeadEntity.class);
        final var testHeadEntity2 = Mockito.mock(HeadEntity.class);
        final var testDatabaseEntity1 = Mockito.mock(DatabaseEntity.class);
        final var testDatabaseEntity2 = Mockito.mock(DatabaseEntity.class);
        final var testLastUpdated1 = LocalDateTime.now().minusHours(6);
        final var testLastUpdated2 = LocalDateTime.now().minusHours(2);

        final var categoryMap = Map.ofEntries(
                Map.entry(testCategoryName1, Set.of(testCategorizable11, testCategorizable12)),
                Map.entry(testCategoryName2, Set.of(testCategorizable21))
        );

        final var categoryUpdateProperties = new CategoriesProperties.Update();
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
        final var testCategoryName1 = "CategoryName1";
        final var testCategoryName2 = "CategoryName2";
        final var testCategorizable11 = Mockito.mock(Categorizable.class);
        final var testCategorizable12 = Mockito.mock(Categorizable.class);
        final var testCategorizable21 = Mockito.mock(Categorizable.class);
        final var testSource1 = "Source1";
        final var testSource2 = "Source2";
        final var testHead1 = Head.builder().name("Head1").build();
        final var testHead2 = Head.builder().name("Head2").build();
        final var testCategoryEntity1 = Mockito.mock(CategoryEntity.class);
        final var testCategoryEntity2 = Mockito.mock(CategoryEntity.class);
        final var testLastUpdated1 = LocalDateTime.now().minusHours(6);
        final var testLastUpdated2 = LocalDateTime.now().minusHours(2);

        final var categoryMap = Map.ofEntries(
                Map.entry(testCategoryName1, Set.of(testCategorizable11, testCategorizable12)),
                Map.entry(testCategoryName2, Set.of(testCategorizable21))
        );

        final var categoryUpdateProperties = new CategoriesProperties.Update();
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
    void updateCategoriesIfNecessaryAsync() {
        // prepare
        try (
                MockedStatic<Bukkit> bukkitMock = Mockito.mockStatic(Bukkit.class);
        ) {
            final var bukkitSchedulerMock = mock(BukkitScheduler.class);
            bukkitMock.when(Bukkit::getScheduler)
                    .thenReturn(bukkitSchedulerMock);
            doNothing().when(categoryUpdater).updateCategoriesIfNecessary();

            // execute
            categoryUpdater.updateCategoriesIfNecessaryAsync();

            // verify
            verify(bukkitSchedulerMock).runTaskAsynchronously(eq(plugin), asyncCaptor.capture());
            asyncCaptor.getValue().run();
            verify(categoryUpdater).updateCategoriesIfNecessary();
        }
    }

    @Test
    void updateCategoriesIfNecessaryNoCategorizablesFound() {
        // prepare
        final var testCategoryName1 = "CategoryName1";
        final var testCategoryName2 = "CategoryName2";
        final var testCategoryEntity1 = Mockito.mock(CategoryEntity.class);
        final var testCategoryEntity2 = Mockito.mock(CategoryEntity.class);
        final var testLastUpdated1 = LocalDateTime.now().minusHours(6);
        final var testLastUpdated2 = LocalDateTime.now().minusHours(2);

        final var categoryMap = Map.ofEntries(
                Map.entry(testCategoryName1, Collections.<Categorizable>emptySet()),
                Map.entry(testCategoryName2, Collections.<Categorizable>emptySet())
        );

        final var categoryUpdateProperties = new CategoriesProperties.Update();
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
        final var testCategoryName1 = "CategoryName1";
        final var testCategoryName2 = "CategoryName2";
        final var testCategorizable1 = Mockito.mock(Categorizable.class);
        final var testCategorizable2 = Mockito.mock(Categorizable.class);

        final var categoryMap = Map.ofEntries(
                Map.entry(testCategoryName1, Set.of(testCategorizable1)),
                Map.entry(testCategoryName2, Set.of(testCategorizable2))
        );

        when(categoryUtils.getCategoryMap())
                .thenReturn(categoryMap);

        // execute
        final var actual = categoryUpdater.getUpdatableCategoryNames(false);

        // verify
        verifyNoMoreInteractions(categoryUtils);
        assertThat(actual, containsInAnyOrder(testCategoryName1, testCategoryName2));
    }

    @Test
    void getUpdatableCategoryNamesNecessaryOnly() {
        // prepare
        final var testCategoryName1 = "CategoryName1";
        final var testCategoryName2 = "CategoryName2";
        final var testCategorizable1 = Mockito.mock(Categorizable.class);
        final var testCategorizable2 = Mockito.mock(Categorizable.class);
        final var testCategoryEntity1 = Mockito.mock(CategoryEntity.class);
        final var testCategoryEntity2 = Mockito.mock(CategoryEntity.class);
        final var testLastUpdated1 = LocalDateTime.now().minusHours(6);
        final var testLastUpdated2 = LocalDateTime.now().minusHours(2);

        final var categoryMap = Map.ofEntries(
                Map.entry(testCategoryName1, Set.of(testCategorizable1)),
                Map.entry(testCategoryName2, Set.of(testCategorizable2))
        );

        final var categoryUpdateProperties = new CategoriesProperties.Update();
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
        final var actual = categoryUpdater.getUpdatableCategoryNames(true);

        // verify
        verifyNoMoreInteractions(categoryUtils, categoryRepository, categoriesProperties);
        verifyNoMoreInteractions(testCategoryEntity1, testCategoryEntity2);
        assertThat(actual, contains(testCategoryName1));
    }
}