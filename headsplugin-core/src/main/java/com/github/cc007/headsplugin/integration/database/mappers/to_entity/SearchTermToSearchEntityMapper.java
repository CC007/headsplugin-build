package com.github.cc007.headsplugin.integration.database.mappers.to_entity;

import com.github.cc007.headsplugin.integration.database.entities.SearchEntity;
import com.github.cc007.headsplugin.integration.database.repositories.SearchRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.collections4.Transformer;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
@Log4j2
public class SearchTermToSearchEntityMapper implements Transformer<String, SearchEntity> {

    private final SearchRepository searchRepository;

    @Override
    public SearchEntity transform(String searchTerm) {
        val optionalSearch = searchRepository.findBySearchTerm(searchTerm);

        SearchEntity search;
        if (!optionalSearch.isPresent()) {
            search = new SearchEntity();
            search.setSearchTerm(searchTerm);
            search.resetSearchCount();
            search.setLastUpdated(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC));
        } else {
            search = optionalSearch.get();
        }
        return searchRepository.save(search);
    }
}
