package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.HeadUpdater;
import com.github.cc007.headsplugin.integration.daos.interfaces.Creatable;
import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;
import com.github.cc007.headsplugin.integration.database.transaction.Transaction;

import lombok.val;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
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
    List<Creatable> creatables;

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
        val testHeadName = "TestHeadName";
        val testDatabaseName = "TestDatabaseName";
        val testHeadOwner = UUID.randomUUID();
        val testPlayer = mock(Player.class);
        val testCreatable = mock(Creatable.class);
        val testHead = Head.builder().headOwner(testHeadOwner).name(testHeadName).build();
        val headEntity1 = mock(HeadEntity.class);
        val headEntity2 = mock(HeadEntity.class);
        val databaseEntity = mock(DatabaseEntity.class);

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
        val actual = headCreator.createHead(testPlayer, testHeadName);

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
        val testHeadName = "TestHeadName";
        val testDatabaseName = "TestDatabaseName";
        val testHeadOwner = UUID.randomUUID();
        val testPlayer = mock(Player.class);
        val testCreatable = mock(Creatable.class);
        val testHead = Head.builder().headOwner(testHeadOwner).name(testHeadName).build();
        val headEntity1 = mock(HeadEntity.class);
        val headEntity2 = mock(HeadEntity.class);
        val databaseEntity = mock(DatabaseEntity.class);

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
        val actual = headCreator.createHead(testPlayer, testHeadName);

        // verify
        assertThat(actual, aMapWithSize(0));
        verify(testCreatable).getDatabaseName(); // only once!
        verifyNoMoreInteractions(testPlayer, testCreatable);
        verifyNoMoreInteractions(headEntity1, headEntity2, databaseEntity);
        verifyNoMoreInteractions(creatables, headUpdater, databaseRepository, transaction);
    }

}