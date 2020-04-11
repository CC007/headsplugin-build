package com.github.cc007.headsplugin.integration.rest.daos.heads.interfaces;

import com.github.cc007.headsplugin.api.business.domain.Head;

import lombok.val;

import java.util.List;
import java.util.Optional;

public interface Searchable extends DatabaseClientDao {
    default Optional<Head> getFirstHead(String searchTerm) {
        val heads = getHeads(searchTerm);
        if (heads.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(heads.get(0));
        }
    }

    List<Head> getHeads(String searchTerm);
}
