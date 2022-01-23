package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.utils.HeadUtils;
import com.github.cc007.headsplugin.business.services.heads.utils.HeadUtilsImpl;
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
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static com.github.cc007.headsplugin.integration.database.entities.HeadEntityMatcher.aHeadEntityThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.isA;
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
        newHeadEntity3.setHeadOwner(headOwner3.toString());

        HeadEntity newHeadEntity4 = new HeadEntity();
        newHeadEntity4.setHeadOwner(headOwner4.toString());


        List<HeadEntity> storedHeads = new ArrayList<>(List.of(headEntity1, headEntity2));

        Collection<Head> foundHeads = List.of(head1, head2, head2, head3, head4, head4);

        when(transaction.runTransacted(isA(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<List<HeadEntity>> supplier = invocation.getArgument(0);
                    return supplier.get();
                });

        when(headUtils.getHeadOwnerStrings(foundHeads))
                .thenCallRealMethod();

        when(headRepository.findAllHeadOwnersByHeadOwnerIn(headOwnerCaptor1.capture()))
                .thenReturn(List.of(
                        headOwner1.toString(),
                        headOwner2.toString()
                ));

        when(headRepository.createFromHead(head3))
                .thenReturn(newHeadEntity3);
        when(headRepository.createFromHead(head4))
                .thenReturn(newHeadEntity4);

        when(headRepository.findAllByHeadOwnerIn(headOwnerCaptor2.capture()))
                .thenReturn(storedHeads);

        // execute
        List<HeadEntity> actual = headUpdater.updateHeads(foundHeads);

        // verify
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
}