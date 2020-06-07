package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.HeadSearcher;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.mappers.from_entity.HeadEntityToHeadMapper;
import com.github.cc007.headsplugin.integration.database.mappers.to_entity.DatabaseNameToDatabaseEntityMapper;
import com.github.cc007.headsplugin.integration.database.mappers.to_entity.SearchTermToSearchEntityMapper;
import com.github.cc007.headsplugin.integration.database.repositories.HeadRepository;
import com.github.cc007.headsplugin.integration.database.repositories.SearchRepository;
import com.github.cc007.headsplugin.integration.daos.heads.interfaces.Searchable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class HeadSearcherImpl implements HeadSearcher {

    public final Map<String, Searchable> searchables;
    public final HeadUpdater headUpdater;
    public final HeadUtils headUtils;
    public final DatabaseNameToDatabaseEntityMapper databaseNameToDatabaseEntityMapper;
    public final SearchTermToSearchEntityMapper searchTermToSearchEntityMapper;
    public final HeadRepository headRepository;
    public final SearchRepository searchRepository;
    public final HeadEntityToHeadMapper headEntityToHeadMapper;

    @Value("${headsplugin.search.update.interval:5}")
    private int searchUpdateInterval;

    @Override
    @Transactional(readOnly = true)
    public int getSearchCount(String searchTerm) {
        val optionalSearchEntity = searchRepository.findBySearchTerm(searchTerm);
        if (!optionalSearchEntity.isPresent()) {
            return 0;
        }

        val searchEntity = optionalSearchEntity.get();
        return searchEntity.getSearchCount();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Head> getHead(UUID headOwner) {
        return headRepository.findByHeadOwner(headOwner.toString()).map(headEntityToHeadMapper::transform);
    }

    @Override
    @Transactional
    public List<Head> getHeads(String searchTerm) {
        long start = System.currentTimeMillis();
        if (needsUpdate(searchTerm)) {
            log.info("Updating heads for: " + searchTerm);
            updateSearch(searchTerm);
        } else {
            log.info("Use cached heads for: " + searchTerm);
        }
        List<Head> storedHeads = getStoredHeads(searchTerm);
        long end = System.currentTimeMillis();
        log.info(String.format("getHeads time: %.3fs", (end - start) / 1000.0));
        return storedHeads;
    }

    private boolean needsUpdate(String searchTerm) {
        val optionalSearchEntity = searchRepository.findBySearchTerm(searchTerm);
        if (!optionalSearchEntity.isPresent()) {
            return true;
        }

        val searchEntity = optionalSearchEntity.get();
        return searchEntity.getLastUpdated()
                .plusMinutes(searchUpdateInterval)
                .isBefore(LocalDateTime.now());
    }

    private List<Head> getStoredHeads(String searchTerm) {
        return searchRepository.findBySearchTerm(searchTerm)
                .orElseThrow(NoResultException::new)
                .getHeads()
                .stream()
                .map(headEntityToHeadMapper::transform)
                .collect(Collectors.toList());
    }

    private void updateSearch(String searchTerm) {
        // get heads based on searchables
        val foundHeadsMap = requestHeads(searchables.values(), searchTerm);
        for (Map.Entry<Searchable, List<Head>> foundHeadsEntries : foundHeadsMap.entrySet()) {
            val searchable = foundHeadsEntries.getKey();
            val foundHeads = foundHeadsEntries.getValue();

            val headEntities = headUpdater.updateHeads(foundHeads);
            updateDatabaseHeads(searchable, headEntities);
            updateSearchHeads(searchTerm, headEntities);
        }

        // get heads that are already stored in the database
        val storedHeadEntities = headRepository.findByNameContaining(searchTerm);
        updateSearchHeads(searchTerm, storedHeadEntities);
    }

    private void updateSearchHeads(String searchTerm, List<HeadEntity> headEntities) {
        val searchEntity = searchTermToSearchEntityMapper.transform(searchTerm);

        headEntities.forEach(searchEntity::addhead);
        searchEntity.setLastUpdated(LocalDateTime.now());
        searchEntity.incrementSearchCount();
        searchRepository.save(searchEntity);
    }

    private void updateDatabaseHeads(Searchable searchable, List<HeadEntity> headEntities) {
        val database = databaseNameToDatabaseEntityMapper.transform(searchable.getDatabaseName());
        headUpdater.updateDatabaseHeads(headEntities, database);
    }

    private Map<Searchable, List<Head>> requestHeads(Collection<Searchable> searchables, String searchTerm) {
        return searchables
                .stream()
                .collect(Collectors.toMap(
                        searchable -> searchable,
                        searchable -> searchable.getHeads(searchTerm)
                ));
    }

}
