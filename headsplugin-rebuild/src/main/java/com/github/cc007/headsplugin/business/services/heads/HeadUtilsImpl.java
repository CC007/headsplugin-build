package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.HeadUtils;

import lombok.NonNull;
import lombok.val;

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
    public int[] getIntArrayFromUuid(@NonNull UUID uuid) {
        val intArray = new int[4];
        val mostSignificantBits = uuid.getMostSignificantBits();
        val leastSignificantBits = uuid.getLeastSignificantBits();
        intArray[0] = (int) (mostSignificantBits >> 32);
        intArray[1] = (int) mostSignificantBits;
        intArray[2] = (int) (leastSignificantBits >> 32);
        intArray[3] = (int) leastSignificantBits;
        return intArray;
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
