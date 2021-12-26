package com.github.cc007.headsplugin.api.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface HeadUtils {

    /**
     * Get a list of head owner UUIDs as strings, based on the provided heads
     *
     * @param heads the heads to get the head owner UUIDs for
     * @return list of head owner UUIDs as strings
     */
    List<String> getHeadOwnerStrings(Collection<Head> heads);

    /**
     * Get a given UUID as an int array.
     * This int array will have a size of 4.
     *
     * @param uuid the UUID to convert
     * @return the resulting int array
     */
    int[] getIntArrayFromUuid(UUID uuid);

    /**
     * Determine if the map of lists contains any values.
     * If there are no keys or if none of the keys return a list with any values, this will be true.
     *
     * @param listMap the map of lists
     * @return whether any heads were found
     */
    boolean isEmpty(Map<?, ? extends List<?>> listMap);
}
