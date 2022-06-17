package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.HeadCreator;
import com.github.cc007.headsplugin.api.business.services.heads.HeadUpdater;
import com.github.cc007.headsplugin.business.utils.OptionalUtils;
import com.github.cc007.headsplugin.integration.daos.interfaces.Creatable;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;
import com.github.cc007.headsplugin.integration.database.transaction.Transaction;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@ExtensionMethod(OptionalUtils.class)
public class HeadCreatorImpl implements HeadCreator {

    private final Set<Creatable> creatables;
    private final HeadUpdater headUpdater;
    private final DatabaseRepository databaseRepository;
    private final Transaction transaction;

    @Override
    public Map<String, Head> createHead(Player player, String newHeadName) {
        final var headOwner = player.getUniqueId();
        final var newHeadsMap = new HashMap<String, Head>();
        transaction.runTransacted(() -> {
            for (Creatable creatable : creatables) {
                final var databaseName = creatable.getDatabaseName();
                creatable.addHead(headOwner, newHeadName)
                        .peek(newHead -> storeHead(databaseName, newHead))
                        .ifPresent(newHead -> newHeadsMap.put(databaseName, newHead));
            }
        });
        return newHeadsMap;
    }

    private void storeHead(String databaseName, Head newHead) {
        final var headEntities = headUpdater.updateHeads(List.of(newHead));
        final var database = databaseRepository.findByOrCreateFromName(databaseName);
        headEntities.forEach(database::addhead);
    }
}
