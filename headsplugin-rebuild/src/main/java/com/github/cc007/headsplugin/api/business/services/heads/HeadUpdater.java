package com.github.cc007.headsplugin.api.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;

import java.util.Collection;
import java.util.List;

public interface HeadUpdater {
    List<HeadEntity> updateHeads(Collection<Head> foundHeads);
}
