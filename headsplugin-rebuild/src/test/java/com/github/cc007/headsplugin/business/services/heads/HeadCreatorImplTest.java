package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.HeadUpdater;
import com.github.cc007.headsplugin.integration.daos.interfaces.Creatable;
import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;
import com.github.cc007.headsplugin.integration.database.transaction.Transaction;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HeadCreatorImplTest {

    @Mock
    Set<Creatable> creatables;

    @Mock
    HeadUpdater headUpdater;

    @Mock
    DatabaseRepository databaseRepository;

    @Mock
    Transaction transaction;

    @InjectMocks
    HeadCreatorImpl headCreator;

    @Test
    void createHeadSuccessfulCreation() {
        // prepare
        final var testHeadName = "TestHeadName";
        final var testDatabaseName = "TestDatabaseName";
        final var testHeadOwner = UUID.randomUUID();
        final var testPlayer = mock(Player.class);
        final var testCreatable = mock(Creatable.class);
        final var testHead = Head.builder().headOwner(testHeadOwner).name(testHeadName).build();
        final var headEntity1 = mock(HeadEntity.class);
        final var headEntity2 = mock(HeadEntity.class);
        final var databaseEntity = mock(DatabaseEntity.class);

        when(testPlayer.getUniqueId())
                .thenReturn(testHeadOwner);
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(transaction).runTransacted(isA(Runnable.class));

        when(creatables.iterator())
                .thenReturn(List.of(testCreatable).iterator());
        when(testCreatable.getDatabaseName())
                .thenReturn(testDatabaseName);
        when(testCreatable.addHead(testHeadOwner, testHeadName))
                .thenReturn(Optional.of(testHead));
        when(headUpdater.updateHeads(argThat(c -> c.contains(testHead))))
                .thenReturn(List.of(headEntity1, headEntity2));
        when(databaseRepository.findByOrCreateFromName(testDatabaseName))
                .thenReturn(databaseEntity);
        doNothing().when(databaseEntity).addhead(headEntity1);
        doNothing().when(databaseEntity).addhead(headEntity2);

        // execute
        final var actual = headCreator.createHead(testPlayer, testHeadName);

        // verify
        assertThat(actual, aMapWithSize(1));
        assertThat(actual, hasEntry(testDatabaseName, testHead));
        verify(testCreatable).getDatabaseName(); // only once!
        verifyNoMoreInteractions(testPlayer, testCreatable);
        verifyNoMoreInteractions(headEntity1, headEntity2, databaseEntity);
        verifyNoMoreInteractions(creatables, headUpdater, databaseRepository, transaction);
    }

    @Test
    void createHeadUnsuccessfulCreation() {
        // prepare
        final var testHeadName = "TestHeadName";
        final var testDatabaseName = "TestDatabaseName";
        final var testHeadOwner = UUID.randomUUID();
        final var testPlayer = mock(Player.class);
        final var testCreatable = mock(Creatable.class);
        final var testHead = Head.builder().headOwner(testHeadOwner).name(testHeadName).build();
        final var headEntity1 = mock(HeadEntity.class);
        final var headEntity2 = mock(HeadEntity.class);
        final var databaseEntity = mock(DatabaseEntity.class);

        when(testPlayer.getUniqueId())
                .thenReturn(testHeadOwner);
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(transaction).runTransacted(isA(Runnable.class));

        when(creatables.iterator())
                .thenReturn(List.of(testCreatable).iterator());
        when(testCreatable.getDatabaseName())
                .thenReturn(testDatabaseName);
        when(testCreatable.addHead(testHeadOwner, testHeadName))
                .thenReturn(Optional.empty());

        // execute
        final var actual = headCreator.createHead(testPlayer, testHeadName);

        // verify
        assertThat(actual, aMapWithSize(0));
        verify(testCreatable).getDatabaseName(); // only once!
        verifyNoMoreInteractions(testPlayer, testCreatable);
        verifyNoMoreInteractions(headEntity1, headEntity2, databaseEntity);
        verifyNoMoreInteractions(creatables, headUpdater, databaseRepository, transaction);
    }

}