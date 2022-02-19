package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.Profiler;
import com.github.cc007.headsplugin.api.business.services.heads.HeadSearcher;
import com.github.cc007.headsplugin.api.business.services.heads.HeadUpdater;
import com.github.cc007.headsplugin.api.business.services.heads.utils.HeadUtils;
import com.github.cc007.headsplugin.config.properties.HeadspluginProperties;
import com.github.cc007.headsplugin.integration.daos.interfaces.DatabaseClientDao;
import com.github.cc007.headsplugin.integration.daos.interfaces.Searchable;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.entities.SearchEntity;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;
import com.github.cc007.headsplugin.integration.database.repositories.HeadRepository;
import com.github.cc007.headsplugin.integration.database.repositories.SearchRepository;
import com.github.cc007.headsplugin.integration.database.transaction.Transaction;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.collections4.Transformer;
import org.apache.logging.log4j.Level;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
public class HeadSearcherImpl implements HeadSearcher {

    private final Set<Searchable> searchables;
    private final HeadUpdater headUpdater;
    private final HeadUtils headUtils;

    private final Transformer<HeadEntity, Head> headEntityToHeadMapper;

    private final HeadRepository headRepository;
    private final SearchRepository searchRepository;
    private final DatabaseRepository databaseRepository;

    private final HeadspluginProperties headspluginProperties;

    private final Transaction transaction;
    private final Profiler profiler;

    @Override
    public long getSearchCount(@NonNull String searchTerm) {
        return transaction.runTransacted(() ->
                searchRepository.findBySearchTerm(searchTerm)
                        .map(SearchEntity::getSearchCount)
                        .orElse(0L)
        );
    }

    @Override
    public Optional<Head> getHead(@NonNull UUID headOwner) {
        return transaction.runTransacted(() ->
                headRepository.findByHeadOwner(headOwner.toString())
                        .map(headEntityToHeadMapper::transform)
        );
    }

    @Override
    public List<Head> getHeads(@NonNull String searchTerm) {
        return profiler.runProfiled(Level.INFO, "Heads for " + searchTerm + " found", () ->
                transaction.runTransacted(() -> {
                    final var search = searchRepository.findByOrCreateFromSearchTerm(searchTerm);
                    updateHeadsIfNecessary(search);
                    return getStoredHeads(search);
                })
        );
    }

    /**
     * Update the heads for a search term that haven't recently been updated.
     * This depends on the property <code>headsplugin.search.update.interval</code> in config.yml.
     *
     * @param search the search entity associated with the search term
     */
    private void updateHeadsIfNecessary(SearchEntity search) {
        val searchTerm = search.getSearchTerm();
        if (!needsUpdate(search)) {
            log.info("Use cached heads for: " + searchTerm);
            return;
        }
        val foundHeadsBySource = requestHeads(searchTerm);
        if (headUtils.isEmpty(foundHeadsBySource)) {
            log.info("No heads found for the search " + searchTerm + ". Skipping the update");
            return;
        }
        log.info("Updating heads for: " + searchTerm);
        updateSearch(search, foundHeadsBySource);
    }

    /**
     * Determine if the cache for a search term needs to be updated
     *
     * @param search the search entity associated with the search term
     * @return whether to update the cache
     */
    private boolean needsUpdate(SearchEntity search) {
        int searchUpdateInterval = headspluginProperties.getSearch().getUpdate().getInterval();
        return search.getLastUpdated()
                .plusMinutes(searchUpdateInterval)
                .isBefore(LocalDateTime.now());
    }

    /**
     * Request the heads for a given search term from the searchable sources
     *
     * @param searchTerm the search term to use when searching for heads
     * @return the heads for that search term, grouped by source
     */
    private Map<String, List<Head>> requestHeads(String searchTerm) {
        return searchables
                .stream()
                .collect(Collectors.toMap(
                        DatabaseClientDao::getDatabaseName,
                        searchable -> searchable.getHeads(searchTerm)
                ));
    }

    /**
     * Update the search cache for a given search term with the given heads
     * <p>
     * Firstly, heads that were already in the database and match the search term will be linked to the search.
     * After that all heads that weren't already in the database will be added.
     * Then all heads will be linked to the given search and data source (like MineSkin), if it wasn't already.
     * This includes both all new heads and the ones that were already in the database.
     * Finally the search count and last updated timestamp will be updated for this search term
     *
     * @param search the search entity associated with the search term
     * @param headsBySource the heads for that search term, grouped by source
     */
    private void updateSearch(SearchEntity search, Map<String, List<Head>> headsBySource) {

        val storedHeadEntities = headRepository.findAllByNameIgnoreCaseContaining(search.getSearchTerm());
        storedHeadEntities.forEach(search::addhead);

        headsBySource.forEach((databaseName, foundHeads) -> {
            val headEntities = headUpdater.updateHeads(foundHeads);
            headEntities.forEach(search::addhead);
            val database = databaseRepository.findByOrCreateFromName(databaseName);
            headEntities.forEach(database::addhead);
        });

        search.setLastUpdated(LocalDateTime.now());
        search.incrementSearchCount();
    }

    /**
     * Get all heads that are linked to a given search term.
     *
     * @param search the search entity associated with the search term
     * @return all heads that are linked to the search term
     */
    private List<Head> getStoredHeads(SearchEntity search) {
        return search.getHeads()
                .stream()
                .map(headEntityToHeadMapper::transform)
                .collect(Collectors.toList());
    }

}
