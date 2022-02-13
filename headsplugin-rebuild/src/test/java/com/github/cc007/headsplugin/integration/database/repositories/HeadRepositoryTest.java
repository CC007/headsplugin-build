package com.github.cc007.headsplugin.integration.database.repositories;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.github.cc007.headsplugin.integration.database.entities.HeadEntityMatcher.aHeadEntityThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HeadRepositoryTest {

    @Spy
    HeadRepository headRepository = new DummyHeadRepository();

    @Test
    void createFromHead() {
        // prepare
        val testHeadOwner = UUID.randomUUID();
        val testName = "Name";
        val testValue = "Value";
        val testHeadDatabase = "HeadDatabase";
        val head = Head.builder()
                .name(testName)
                .value(testValue)
                .headOwner(testHeadOwner)
                .headDatabase(testHeadDatabase)
                .build();

        when(headRepository.manageNew())
                .thenReturn(new HeadEntity());

        // execute
        val actual = headRepository.createFromHead(head);

        // verify
        assertThat(actual, is(aHeadEntityThat()
                .hasName(testName)
                .hasValue(testValue)
                .hasHeadOwner(testHeadOwner.toString())));
    }
}

class DummyHeadRepository implements HeadRepository {

    @Override
    public Optional<HeadEntity> findByHeadOwner(String headOwner) {
        return Optional.empty();
    }

    @Override
    public List<HeadEntity> findAllByHeadOwnerIn(Collection<String> headOwners) {
        return null;
    }

    @Override
    public List<String> findAllHeadOwnersByHeadOwnerIn(Collection<String> headOwners) {
        return null;
    }

    @Override
    public List<HeadEntity> findAllByDatabases_NameAndHeadOwnerIn(String databaseName, Collection<String> headOwners) {
        return null;
    }

    @Override
    public List<HeadEntity> findAllByNameIgnoreCaseContaining(String name) {
        return null;
    }

    @Override
    public HeadEntity manageNew() {
        return null;
    }
}