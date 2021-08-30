package com.github.cc007.headsplugin.api.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface HeadUtils {
    List<Head> flattenHeads(Collection<List<Head>> heads);

    List<String> getHeadOwnerStrings(Collection<Head> heads);

    int[] getIntArrayFromUuid(UUID uuid);
}
