package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.Profiler;
import com.github.cc007.headsplugin.api.business.services.heads.HeadUpdater;
import com.github.cc007.headsplugin.api.business.services.heads.utils.HeadUtils;
import com.github.cc007.headsplugin.business.services.heads.utils.HeadUtilsImpl;
import com.github.cc007.headsplugin.config.properties.HeadspluginProperties;
import com.github.cc007.headsplugin.integration.daos.interfaces.Searchable;
import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.entities.SearchEntity;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;
import com.github.cc007.headsplugin.integration.database.repositories.HeadRepository;
import com.github.cc007.headsplugin.integration.database.repositories.SearchRepository;
import com.github.cc007.headsplugin.integration.database.transaction.Transaction;

import lombok.val;
import org.apache.commons.collections4.Transformer;
import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HeadSearcherImplTest {

    @Mock
    Set<Searchable> searchables;

    @Mock
    HeadUpdater headUpdater;

    @Spy
    HeadUtils headUtils = new HeadUtilsImpl();

    @Mock
    Transformer<HeadEntity, Head> headEntityToHeadMapper;

    @Mock
    HeadRepository headRepository;

    @Mock
    SearchRepository searchRepository;

    @Mock
    DatabaseRepository databaseRepository;

    @Mock
    HeadspluginProperties headspluginProperties;

    @Mock
    Transaction transaction;

    @Mock
    Profiler profiler;

    @InjectMocks
    HeadSearcherImpl headSearcher;

    @Test
    void getSearchCount() {
        // prepare
        val existingSearchTerm = "ExistingSearchTerm";

        val searchEntity = new SearchEntity();
        searchEntity.incrementSearchCount();
        searchEntity.incrementSearchCount();

        when(transaction.runTransacted(isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(0);
                    return supplier.get();
                });
        when(searchRepository.findBySearchTerm(existingSearchTerm))
                .thenReturn(Optional.of(searchEntity));

        // execute
        val actual = headSearcher.getSearchCount(existingSearchTerm);

        // verify
        assertThat(actual, is(2L));
        verifyNoMoreInteractions(transaction, searchRepository);
    }

    @Test
    void getSearchCountSearchTermNotFound() {
        // prepare
        val newSearchTerm = "NewSearchTerm";

        when(transaction.runTransacted(isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(0);
                    return supplier.get();
                });
        when(searchRepository.findBySearchTerm(newSearchTerm))
                .thenReturn(Optional.empty());

        // execute
        val actual = headSearcher.getSearchCount(newSearchTerm);

        // verify
        assertThat(actual, is(0L));
        verifyNoMoreInteractions(transaction, searchRepository);
    }

    @Test
    void getSearchCountSearchTermNull() {
        // prepare

        // execute
        val actualException = Assertions.assertThrows(NullPointerException.class,
                () -> headSearcher.getSearchCount(null)
        );

        // verify
        assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        verifyNoMoreInteractions(transaction, searchRepository);
    }

    @Test
    void getHead() {
        // prepare
        val testHeadOwner = UUID.randomUUID();
        val testHeadEntity = mock(HeadEntity.class);
        val testHead = Head.builder().headOwner(testHeadOwner).build();

        when(transaction.runTransacted(isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(0);
                    return supplier.get();
                });
        when(headRepository.findByHeadOwner(testHeadOwner.toString()))
                .thenReturn(Optional.of(testHeadEntity));
        when(headEntityToHeadMapper.transform(testHeadEntity))
                .thenReturn(testHead);

        // execute
        val actual = headSearcher.getHead(testHeadOwner);

        // verify
        assertThat(actual, isPresentAndIs(testHead));
        verifyNoMoreInteractions(transaction, headRepository, headEntityToHeadMapper);
    }

    @Test
    void getHeadHeadOwnerNotFound() {
        // prepare
        val testHeadOwner = UUID.randomUUID();

        when(transaction.runTransacted(isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(0);
                    return supplier.get();
                });
        when(headRepository.findByHeadOwner(testHeadOwner.toString()))
                .thenReturn(Optional.empty());

        // execute
        val actual = headSearcher.getHead(testHeadOwner);

        // verify
        assertThat(actual, isEmpty());
        verifyNoMoreInteractions(transaction, headRepository, headEntityToHeadMapper);
    }

    @Test
    void getHeadHeadOwnerNull() {
        // prepare

        // execute
        val actualException = Assertions.assertThrows(NullPointerException.class,
                () -> headSearcher.getHead(null)
        );

        // verify
        assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        verifyNoMoreInteractions(transaction, headRepository, headEntityToHeadMapper);
    }

    @Test
    void getHeadsSearchTermFound() {
        // prepare
        val testSearchTerm = "SearchTerm";

        val testSearchEntity = mock(SearchEntity.class);
        val testSearchProperties = new HeadspluginProperties.Search();
        testSearchProperties.getUpdate().setInterval(4);
        val testHeadEntity1 = mock(HeadEntity.class);
        val testHeadEntity2 = mock(HeadEntity.class);
        val testHead1 = Head.builder().name("Head1").build();
        val testHead2 = Head.builder().name("Head2").build();

        when(profiler.runProfiled(eq(Level.INFO), contains(testSearchTerm), isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(2);
                    return supplier.get();
                });
        when(transaction.runTransacted(isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(0);
                    return supplier.get();
                });
        when(searchRepository.findByOrCreateFromSearchTerm(testSearchTerm))
                .thenReturn(testSearchEntity);

        when(testSearchEntity.getSearchTerm())
                .thenReturn(testSearchTerm);
        when(headspluginProperties.getSearch())
                .thenReturn(testSearchProperties);
        when(testSearchEntity.getLastUpdated())
                .thenReturn(LocalDateTime.now().minusMinutes(2));

        when(testSearchEntity.getHeads())
                .thenReturn(Set.of(testHeadEntity1, testHeadEntity2));
        when(headEntityToHeadMapper.transform(testHeadEntity1))
                .thenReturn(testHead1);
        when(headEntityToHeadMapper.transform(testHeadEntity2))
                .thenReturn(testHead2);

        // execute
        val actual = headSearcher.getHeads(testSearchTerm);

        // verify
        assertThat(actual, containsInAnyOrder(testHead1, testHead2));
        verifyNoMoreInteractions(testSearchEntity, testHeadEntity1, testHeadEntity2);
        verifyNoMoreInteractions(profiler, transaction, searchRepository, headspluginProperties, headEntityToHeadMapper);
        verifyNoInteractions(searchables, headUpdater, headUtils, databaseRepository);
    }

    @Test
    void getHeadsSearchTermNeedsUpdateHeadsNotFound() {
        // prepare
        val testSearchTerm = "SearchTerm";
        val testDatabaseName = "DatabaseName";

        val testSearchEntity = mock(SearchEntity.class);
        val testSearchProperties = new HeadspluginProperties.Search();
        testSearchProperties.getUpdate().setInterval(4);
        val testSearchable = mock(Searchable.class);
        val testHeadEntity1 = mock(HeadEntity.class);
        val testHeadEntity2 = mock(HeadEntity.class);
        val testHead1 = Head.builder().name("Head1").build();
        val testHead2 = Head.builder().name("Head2").build();

        when(profiler.runProfiled(eq(Level.INFO), contains(testSearchTerm), isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(2);
                    return supplier.get();
                });
        when(transaction.runTransacted(isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(0);
                    return supplier.get();
                });
        when(searchRepository.findByOrCreateFromSearchTerm(testSearchTerm))
                .thenReturn(testSearchEntity);

        when(testSearchEntity.getSearchTerm())
                .thenReturn(testSearchTerm);
        when(headspluginProperties.getSearch())
                .thenReturn(testSearchProperties);
        when(testSearchEntity.getLastUpdated())
                .thenReturn(LocalDateTime.now().minusMinutes(6));

        when(searchables.stream())
                .thenReturn(Stream.of(testSearchable));
        when(testSearchable.getDatabaseName())
                .thenReturn(testDatabaseName);
        when(testSearchable.getHeads(testSearchTerm))
                .thenReturn(List.of());

        when(testSearchEntity.getHeads())
                .thenReturn(Set.of(testHeadEntity1, testHeadEntity2));
        when(headEntityToHeadMapper.transform(testHeadEntity1))
                .thenReturn(testHead1);
        when(headEntityToHeadMapper.transform(testHeadEntity2))
                .thenReturn(testHead2);

        // execute
        val actual = headSearcher.getHeads(testSearchTerm);

        // verify
        assertThat(actual, containsInAnyOrder(testHead1, testHead2));
        verifyNoMoreInteractions(testSearchEntity, testHeadEntity1, testHeadEntity2, testSearchable);
        verifyNoMoreInteractions(profiler, transaction, searchRepository, headspluginProperties, headEntityToHeadMapper);
        verifyNoMoreInteractions(searchables);
        verifyNoInteractions(headUpdater, databaseRepository);
    }

    @Test
    void getHeadsSearchTermNeedsUpdateHeadsFound() {
        // prepare
        val testSearchTerm = "SearchTerm";
        val testDatabaseName = "DatabaseName";

        val testSearchEntity = mock(SearchEntity.class);
        val testSearchProperties = new HeadspluginProperties.Search();
        testSearchProperties.getUpdate().setInterval(4);
        val testSearchable = mock(Searchable.class);
        val testHeadEntity1 = mock(HeadEntity.class);
        val testHeadEntity2 = mock(HeadEntity.class);
        val testNewHeadEntity1 = mock(HeadEntity.class);
        val testNewHeadEntity2 = mock(HeadEntity.class);
        val testDatabaseEntity = mock(DatabaseEntity.class);
        val testSearchTermHeadEntity = mock(HeadEntity.class);
        val testHead1 = Head.builder().name("Head1").build();
        val testHead2 = Head.builder().name("Head2").build();
        val testNewHead1 = Head.builder().name("NewHead1").build();
        val testNewHead2 = Head.builder().name("NewHead2").build();
        val testNewHeads = List.of(testNewHead1, testNewHead2);
        val testSearchTermHead = Head.builder().name("SearchTermHead").build();

        when(profiler.runProfiled(eq(Level.INFO), contains(testSearchTerm), isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(2);
                    return supplier.get();
                });
        when(transaction.runTransacted(isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(0);
                    return supplier.get();
                });
        when(searchRepository.findByOrCreateFromSearchTerm(testSearchTerm))
                .thenReturn(testSearchEntity);

        when(testSearchEntity.getSearchTerm())
                .thenReturn(testSearchTerm);
        when(headspluginProperties.getSearch())
                .thenReturn(testSearchProperties);
        when(testSearchEntity.getLastUpdated())
                .thenReturn(LocalDateTime.now().minusMinutes(6));

        when(searchables.stream())
                .thenReturn(Stream.of(testSearchable));
        when(testSearchable.getDatabaseName())
                .thenReturn(testDatabaseName);
        when(testSearchable.getHeads(testSearchTerm))
                .thenReturn(testNewHeads);

        when(headRepository.findAllByNameIgnoreCaseContaining(testSearchTerm))
                .thenReturn(List.of(testSearchTermHeadEntity));
        doNothing().when(testSearchEntity).addhead(testSearchTermHeadEntity);
        when(headUpdater.updateHeads(testNewHeads))
                .thenReturn(List.of(testNewHeadEntity1, testNewHeadEntity2));
        doNothing().when(testSearchEntity).addhead(testNewHeadEntity1);
        doNothing().when(testSearchEntity).addhead(testNewHeadEntity2);
        when(databaseRepository.findByOrCreateFromName(testDatabaseName))
                .thenReturn(testDatabaseEntity);
        doNothing().when(testDatabaseEntity).addhead(testNewHeadEntity1);
        doNothing().when(testDatabaseEntity).addhead(testNewHeadEntity2);
        doNothing().when(testSearchEntity).setLastUpdated(any());
        doNothing().when(testSearchEntity).incrementSearchCount();

        when(testSearchEntity.getHeads())
                .thenReturn(Set.of(
                        testHeadEntity1,
                        testHeadEntity2,
                        testNewHeadEntity1,
                        testNewHeadEntity2,
                        testSearchTermHeadEntity
                ));
        when(headEntityToHeadMapper.transform(testHeadEntity1))
                .thenReturn(testHead1);
        when(headEntityToHeadMapper.transform(testHeadEntity2))
                .thenReturn(testHead2);
        when(headEntityToHeadMapper.transform(testNewHeadEntity1))
                .thenReturn(testNewHead1);
        when(headEntityToHeadMapper.transform(testNewHeadEntity2))
                .thenReturn(testNewHead2);
        when(headEntityToHeadMapper.transform(testSearchTermHeadEntity))
                .thenReturn(testSearchTermHead);

        // execute
        val actual = headSearcher.getHeads(testSearchTerm);

        // verify
        assertThat(actual, containsInAnyOrder(testHead1, testHead2, testNewHead1, testNewHead2, testSearchTermHead));
        verifyNoMoreInteractions(testSearchEntity, testHeadEntity1, testHeadEntity2, testSearchable);
        verifyNoMoreInteractions(testNewHeadEntity1, testNewHeadEntity2, testSearchTermHeadEntity, testDatabaseEntity);
        verifyNoMoreInteractions(profiler, transaction, searchRepository, headspluginProperties, headEntityToHeadMapper);
        verifyNoMoreInteractions(searchables, headUpdater, databaseRepository);
    }

    @Test
    void getHeadsSearchTermNull() {
        // prepare

        // execute
        val actualException = Assertions.assertThrows(NullPointerException.class,
                () -> headSearcher.getHeads(null)
        );

        // verify
        assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
    }
}