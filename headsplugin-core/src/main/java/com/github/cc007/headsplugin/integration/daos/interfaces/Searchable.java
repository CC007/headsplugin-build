package com.github.cc007.headsplugin.integration.daos.interfaces;

import com.github.cc007.headsplugin.api.business.domain.Head;

import java.util.List;
import java.util.Optional;

public interface Searchable extends DatabaseClientDao {
    default Optional<Head> getFirstHead(String searchTerm) {
        return getHeads(searchTerm).stream().findFirst();
    }

    List<Head> getHeads(String searchTerm);
}
