package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.business.domain.Head;
import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.mappers.to_entity.HeadToHeadEntityMapper;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;
import com.github.cc007.headsplugin.integration.database.repositories.HeadRepository;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HeadUpdater {

    private final HeadToHeadEntityMapper headToHeadEntityMapper;
    private final HeadRepository headRepository;
    private final DatabaseRepository databaseRepository;
    private final HeadUtils headUtils;

    public void updateHeads(List<Head> foundHeads) {
        val foundHeadOwnerStrings = headUtils.getHeadOwnerStrings(foundHeads);
        val storedHeadOwnerStrings = getStoredHeadOwnerStringsFromFound(foundHeadOwnerStrings);

        val newHeads = foundHeads.stream()
                .filter(head -> !storedHeadOwnerStrings.contains(head.getHeadOwner().toString()))
                .collect(Collectors.toList());

        val newHeadEntities = newHeads.stream()
                .map(headToHeadEntityMapper::transform)
                .collect(Collectors.toList());

        headRepository.saveAll(newHeadEntities);
    }

    public void updateDatabaseHeads(List<Head> foundHeads, DatabaseEntity database) {
        val foundHeadOwnerStrings = headUtils.getHeadOwnerStrings(foundHeads);
        val databaseHeadOwnerStrings = getDatabaseHeadOwnerStringsFromFound(
                database.getName(),
                foundHeadOwnerStrings
        );

        foundHeads.stream()
                .filter(head -> !databaseHeadOwnerStrings.contains(head.getHeadOwner().toString()))
                .map(headToHeadEntityMapper::transform)
                .forEach(database::addhead);
        databaseRepository.save(database);
    }

    private List<String> getStoredHeadOwnerStringsFromFound(List<String> foundHeadOwnerStrings) {
        return headRepository.findByHeadOwnerIn(foundHeadOwnerStrings)
                .stream()
                .map(HeadEntity::getHeadOwner)
                .collect(Collectors.toList());
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
