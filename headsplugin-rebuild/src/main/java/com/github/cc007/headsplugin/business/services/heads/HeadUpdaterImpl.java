package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.HeadUpdater;
import com.github.cc007.headsplugin.api.business.services.heads.HeadUtils;
import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;
import com.github.cc007.headsplugin.integration.database.repositories.HeadRepository;
import com.github.cc007.headsplugin.integration.database.transaction.Transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
public class HeadUpdaterImpl implements HeadUpdater {

    private final HeadRepository headRepository;
    private final DatabaseRepository databaseRepository;
    private final HeadUtils headUtils;
    private final Transaction transaction;

    @Override
    public List<HeadEntity> updateHeads(Collection<Head> foundHeads) {
        return transaction.runTransacted(() -> {
            val foundHeadOwnerStrings = headUtils.getHeadOwnerStrings(foundHeads);
            val storedHeadOwnerStrings = headRepository.findAllHeadOwnersByHeadOwnerIn(foundHeadOwnerStrings);

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
                    .map(this::createNewHeadEntity)
                    .collect(Collectors.toList());

            val storedHeads = headRepository.findAllByHeadOwnerIn(foundHeadOwnerStrings);
            storedHeads.addAll(newHeadEntities);
            return storedHeads;
        });
    }

    private HeadEntity createNewHeadEntity(Head head) {
        val headEntity = headRepository.manageNew();
        headEntity.setHeadOwner(head.getHeadOwner().toString());
        headEntity.setName(head.getName());
        headEntity.setValue(head.getValue());
        return headEntity;
    }

    @Override
    public void updateDatabaseHeads(List<HeadEntity> foundHeadEntities, DatabaseEntity databaseEntity) {
        foundHeadEntities.forEach(databaseEntity::addhead);
    }

}
