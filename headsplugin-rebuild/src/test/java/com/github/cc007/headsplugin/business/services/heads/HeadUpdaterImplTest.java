package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.HeadUtils;
import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;
import com.github.cc007.headsplugin.integration.database.repositories.HeadRepository;
import com.github.cc007.headsplugin.integration.database.transaction.Transaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static com.github.cc007.headsplugin.integration.database.entities.HeadEntityMatcher.aHeadEntityThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HeadUpdaterImplTest {


    @Mock
    private HeadRepository headRepository;

    @Mock
    private DatabaseRepository databaseRepository;

    @Spy
    private HeadUtils headUtils = new HeadUtilsImpl();

    @Mock
    private Transaction transaction;

    @InjectMocks
    private HeadUpdaterImpl headUpdater;

    @Captor
    private ArgumentCaptor<List<String>> headOwnerCaptor1;
    @Captor
    private ArgumentCaptor<List<String>> headOwnerCaptor2;


    @Test
    void updateHeads() {
        // prepare
        UUID headOwner1 = UUID.randomUUID();
        UUID headOwner2 = UUID.randomUUID();
        UUID headOwner3 = UUID.randomUUID();
        UUID headOwner4 = UUID.randomUUID();

        Head head1 = Head.builder().headOwner(headOwner1).build();
        Head head2 = Head.builder().headOwner(headOwner2).build();
        Head head3 = Head.builder().headOwner(headOwner3).build();
        Head head4 = Head.builder().headOwner(headOwner4).build();

        HeadEntity headEntity1 = new HeadEntity();
        headEntity1.setHeadOwner(headOwner1.toString());

        HeadEntity headEntity2 = new HeadEntity();
        headEntity2.setHeadOwner(headOwner2.toString());

        HeadEntity newHeadEntity3 = new HeadEntity();
        HeadEntity newHeadEntity4 = new HeadEntity();


        List<HeadEntity> storedHeads = new ArrayList<>(Arrays.asList(headEntity1, headEntity2));

        Collection<Head> foundHeads = Arrays.asList(head1, head2, head2, head3, head4, head4);

        when(transaction.runTransacted(isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<List<HeadEntity>> supplier = invocation.getArgument(0);
                    return supplier.get();
                });

        when(headUtils.getHeadOwnerStrings(foundHeads))
                .thenCallRealMethod();

        when(headRepository.findAllHeadOwnersByHeadOwnerIn(headOwnerCaptor1.capture()))
                .thenReturn(Arrays.asList(
                        headOwner1.toString(),
                        headOwner2.toString()
                ));

        when(headRepository.manageNew())
                .thenReturn(newHeadEntity3, newHeadEntity4);

        when(headRepository.findAllByHeadOwnerIn(headOwnerCaptor2.capture()))
                .thenReturn(storedHeads);

        // execute
        List<HeadEntity> actual = headUpdater.updateHeads(foundHeads);

        // verify
        verify(headRepository, times(2)).manageNew();
        verifyNoMoreInteractions(headRepository, databaseRepository, headUtils, transaction);
        assertThat(headOwnerCaptor1.getValue(), contains(
                headOwner1.toString(),
                headOwner2.toString(),
                headOwner2.toString(),
                headOwner3.toString(),
                headOwner4.toString(),
                headOwner4.toString()
        ));
        assertThat(headOwnerCaptor2.getValue(), contains(
                headOwner1.toString(),
                headOwner2.toString(),
                headOwner2.toString(),
                headOwner3.toString(),
                headOwner4.toString(),
                headOwner4.toString()
        ));
        assertThat(actual, containsInAnyOrder(
                aHeadEntityThat()
                        .hasHeadOwner(headOwner1.toString()),
                aHeadEntityThat()
                        .hasHeadOwner(headOwner2.toString()),
                aHeadEntityThat()
                        .hasHeadOwner(headOwner3.toString()),
                aHeadEntityThat()
                        .hasHeadOwner(headOwner4.toString())
        ));
    }

    @Test
    void updateDatabaseHeads() {
        // prepare
        DatabaseEntity databaseEntity = new DatabaseEntity();
        HeadEntity headEntity1 = new HeadEntity();
        headEntity1.setHeadOwner("HeadOwner1");
        HeadEntity headEntity2 = new HeadEntity();
        headEntity1.setHeadOwner("HeadOwner2");
        HeadEntity headEntity3 = new HeadEntity();
        headEntity1.setHeadOwner("HeadOwner3");
        HeadEntity headEntity4 = new HeadEntity();
        headEntity1.setHeadOwner("HeadOwner4");
        databaseEntity.addhead(headEntity1);
        databaseEntity.addhead(headEntity2);

        List<HeadEntity> foundHeadEntities = Arrays.asList(headEntity3, headEntity4);

        // execute
        headUpdater.updateDatabaseHeads(foundHeadEntities, databaseEntity);

        // verify
        assertThat(databaseEntity.getHeads(), containsInAnyOrder(
                headEntity1,
                headEntity2,
                headEntity3,
                headEntity4
        ));
    }
}