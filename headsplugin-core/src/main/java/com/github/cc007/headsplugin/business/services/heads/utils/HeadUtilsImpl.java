package com.github.cc007.headsplugin.business.services.heads.utils;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.utils.HeadUtils;

import lombok.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class HeadUtilsImpl implements HeadUtils {

    @Override
    public List<String> getHeadOwnerStrings(@NonNull Collection<Head> heads) {
        return heads.stream()
                .map(Head::getHeadOwner)
                .map(UUID::toString)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isEmpty(@NonNull Map<?, ? extends List<?>> listMap) {
        if (listMap.isEmpty()) {
            return true;
        }
        return listMap.values()
                .stream()
                .allMatch(Collection::isEmpty);
    }
}
