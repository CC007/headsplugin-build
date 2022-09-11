package com.github.cc007.headsplugin.integration.daos.interfaces;

import com.github.cc007.headsplugin.api.business.domain.Head;

import java.util.Optional;
import java.util.UUID;

public interface Creatable extends DatabaseClientDao {
    Optional<Head> addHead(UUID playerUuid, String newHeadName);
}
