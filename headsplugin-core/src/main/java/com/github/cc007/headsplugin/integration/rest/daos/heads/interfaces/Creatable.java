package com.github.cc007.headsplugin.integration.rest.daos.heads.interfaces;

import com.github.cc007.headsplugin.api.business.domain.Head;

import java.util.Optional;

public interface Creatable extends DatabaseClientDao {
    Optional<Head> addHead(Head newHead);
}
