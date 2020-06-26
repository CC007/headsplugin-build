package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;

import lombok.val;
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

    public int[] getIntArrayFromUuid(UUID uuid) {
        val intArray = new int[4];
        val mostSignificantBits = uuid.getMostSignificantBits();
        val leastSignificantBits = uuid.getLeastSignificantBits();
        intArray[0] = (int) (mostSignificantBits >> 32);
        intArray[1] = (int) mostSignificantBits;
        intArray[2] = (int) (leastSignificantBits >> 32);
        intArray[3] = (int) mostSignificantBits;
        return intArray;
    }
}
