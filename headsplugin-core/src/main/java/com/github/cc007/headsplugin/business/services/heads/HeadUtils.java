package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class HeadUtils {

    public List<Head> flattenHeads(Collection<List<Head>> heads) {
        return heads.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<String> getHeadOwnerStrings(Collection<Head> heads) {
        return heads.stream()
                .map(Head::getHeadOwner)
                .map(UUID::toString)
                .collect(Collectors.toList());
    }
}
