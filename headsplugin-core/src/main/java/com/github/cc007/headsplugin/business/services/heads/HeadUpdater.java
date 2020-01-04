package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.business.domain.Head;
import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.mappers.to_entity.HeadToHeadEntityMapper;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;
import com.github.cc007.headsplugin.integration.database.repositories.HeadRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
@Slf4j
public class HeadUpdater {

    private final HeadToHeadEntityMapper headToHeadEntityMapper;
    private final HeadRepository headRepository;
    private final DatabaseRepository databaseRepository;
    private final HeadUtils headUtils;

    public List<HeadEntity> updateHeads(List<Head> foundHeads) {
        val foundHeadOwnerStrings = headUtils.getHeadOwnerStrings(foundHeads);
        val foundHeadOwnerStringSet = new HashSet<String>(foundHeadOwnerStrings);

//        if(foundHeadOwnerStrings.size() != foundHeadOwnerStringSet.size()) {
//            StringBuilder errorMessage = new StringBuilder().append("Duplicate HeadOwners when updating heads. Heads found:\n");
//            foundHeads.forEach(head -> {
//                errorMessage.append(head.getName())
//                        .append(" (")
//                        .append(head.getHeadOwner())
//                        .append(")\n");
//            });
//            throw new RuntimeException(errorMessage.toString());
//        }
        val headEntities = foundHeadOwnerStringSet.stream()
                .map(s -> foundHeads.stream()
                        .filter(head -> head.getHeadOwner()
                                .toString()
                                .equals(s))
                        .findFirst()
                        .orElseThrow(NullPointerException::new))
                .map(headToHeadEntityMapper::transform)
                .collect(Collectors.toList());
        val savedHeadEntities = headRepository.saveAll(headEntities);
        return StreamSupport.stream(savedHeadEntities.spliterator(), false).collect(Collectors.toList());

//
//        val storedHeadOwnerStrings = getStoredHeadOwnerStringsFromFound(foundHeadOwnerStrings);
//
//        val newHeads = foundHeads.stream()
//                .filter(head -> !storedHeadOwnerStrings.contains(head.getHeadOwner().toString()))
//                //.peek(head -> log.info("Found new head: " + head.getName() + " (" + head.getHeadOwner() + ")."))
//                .collect(Collectors.toList());
//
//        val newHeadEntities = newHeads.stream()
//                .map(headToHeadEntityMapper::transform)
//                .collect(Collectors.toList());
//
//        return headRepository.saveAll(newHeadEntities);
    }

    public void updateDatabaseHeads(List<HeadEntity> foundHeadEntities, DatabaseEntity database) {
        foundHeadEntities.forEach(database::addhead);

//        val foundHeadOwnerStrings = headUtils.getHeadOwnerStrings(foundHeadEntities);
//        val databaseHeadOwnerStrings = getDatabaseHeadOwnerStringsFromFound(
//                database.getName(),
//                foundHeadOwnerStrings
//        );
//
//        foundHeadEntities.stream()
//                .filter(head -> !databaseHeadOwnerStrings.contains(head.getHeadOwner().toString()))
//                //.peek(head -> log.info("Found database for head: " + head.getName() + " (" + head.getHeadOwner() + "): " + database.getName() + "."))
//                .map(headToHeadEntityMapper::transform)
//                .forEach(database::addhead);
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
