package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.mappers.to_entity.HeadToDetachedHeadEntityMapper;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;
import com.github.cc007.headsplugin.integration.database.repositories.HeadRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
@Slf4j
public class HeadUpdater {

    private final HeadToDetachedHeadEntityMapper headToDetachedHeadEntityMapper;
    private final HeadRepository headRepository;
    private final DatabaseRepository databaseRepository;
    private final HeadUtils headUtils;

    public List<HeadEntity> updateHeads(List<Head> foundHeads) {
        val foundHeadOwnerStrings = headUtils.getHeadOwnerStrings(foundHeads);
        val storedHeads = headRepository.findByHeadOwnerIn(foundHeadOwnerStrings);

        val storedHeadOwnerStrings = storedHeads.stream()
                .map(HeadEntity::getHeadOwner)
                .collect(Collectors.toList());

        val newHeads = new ArrayList<Head>();
        val newHeadOwnerStrings = new ArrayList<String>();
        foundHeads.stream()
                .filter(foundHead -> !storedHeadOwnerStrings.contains(foundHead.getHeadOwner().toString()))
                .filter(foundHead -> !newHeadOwnerStrings.contains(foundHead.getHeadOwner().toString()))
                .forEach(foundHead -> {
                    newHeads.add(foundHead);
                    newHeadOwnerStrings.add(foundHead.getHeadOwner().toString());
                });

        val newHeadEntities = newHeads.stream()
                .map(headToDetachedHeadEntityMapper::transform)
                .collect(Collectors.toList());

        val storedNewHeads = headRepository.saveAll(newHeadEntities);

        storedHeads.addAll(StreamSupport.stream(storedNewHeads.spliterator(), false)
                .collect(Collectors.toList()));

        return storedHeads;
    }

    public void updateDatabaseHeads(List<HeadEntity> foundHeadEntities, DatabaseEntity database) {
        foundHeadEntities.forEach(database::addhead);
        databaseRepository.save(database);
    }

    private List<String> getDatabaseHeadOwnerStringsFromFound(String databaseName, List<String> foundHeadOwnerStrings) {
        return headRepository.findByDatabases_NameAndHeadOwnerIn(databaseName, foundHeadOwnerStrings)
                .stream()
                .map(HeadEntity::getHeadOwner)
                .collect(Collectors.toList());
    }

    private String getHeadString(HeadEntity head) {
        return head.getName()
                + " (" + getHeadDatabaseString(head)
                + ":" + head.getHeadOwner()
                + ")";
    }

    private String getHeadDatabaseString(HeadEntity head) {
        Set<DatabaseEntity> headDatabases = head.getDatabases();
        return (headDatabases != null ? headDatabases : new HashSet<DatabaseEntity>())
                .stream()
                .map(DatabaseEntity::getName)
                .collect(Collectors.joining("/"));
    }

}
