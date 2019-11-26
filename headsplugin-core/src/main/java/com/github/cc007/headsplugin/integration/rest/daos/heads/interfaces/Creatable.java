package com.github.cc007.headsplugin.integration.rest.daos.heads.interfaces;

import com.github.cc007.headsplugin.business.domain.Head;

import java.util.Optional;

public interface Creatable {
    Optional<Head> addHead(Head newHead);
}
