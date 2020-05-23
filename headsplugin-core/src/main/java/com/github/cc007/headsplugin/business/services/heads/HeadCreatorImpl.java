package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.HeadCreator;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.mappers.to_entity.DatabaseNameToDatabaseEntityMapper;
import com.github.cc007.headsplugin.integration.rest.daos.heads.interfaces.Creatable;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HeadCreatorImpl implements HeadCreator {

    private final List<Creatable> creatables;
    private final HeadUpdater headUpdater;
    public final DatabaseNameToDatabaseEntityMapper databaseNameToDatabaseEntityMapper;

    @Override
    @Transactional
    public Map<String, Head> createHead(Player player, String newHeadName) {
        val newHeadsMap = new HashMap<String, Head>();
        for (Creatable creatable : creatables) {
            val optionalNewHead = creatable.addHead(player.getUniqueId(), newHeadName);
            if (optionalNewHead.isPresent()) {
                val newHead = optionalNewHead.get();
                val headEntities = headUpdater.updateHeads(Collections.singletonList(newHead));
                updateDatabaseHeads(creatable, headEntities);
                newHeadsMap.put(creatable.getDatabaseName(), newHead);
            }
        }
        return newHeadsMap;
    }

    private void updateDatabaseHeads(Creatable creatable, List<HeadEntity> headEntities) {
        val database = databaseNameToDatabaseEntityMapper.transform(creatable.getDatabaseName());
        headUpdater.updateDatabaseHeads(headEntities, database);
    }
}
