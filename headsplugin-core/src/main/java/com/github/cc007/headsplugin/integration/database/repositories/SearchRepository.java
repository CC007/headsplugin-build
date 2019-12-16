package com.github.cc007.headsplugin.integration.database.repositories;

import com.github.cc007.headsplugin.integration.database.entities.SearchEntity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRepository extends CrudRepository<SearchEntity, Long> {

    List<SearchEntity> findBySearchTerm(String searchTerm);
}
