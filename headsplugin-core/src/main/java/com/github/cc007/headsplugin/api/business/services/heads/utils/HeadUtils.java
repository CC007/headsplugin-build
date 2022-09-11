package com.github.cc007.headsplugin.api.business.services.heads.utils;

import com.github.cc007.headsplugin.api.business.domain.Head;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface HeadUtils {

    /**
     * Get a list of head owner UUIDs as strings, based on the provided heads
     *
     * @param heads the heads to get the head owner UUIDs for
     * @return list of head owner UUIDs as strings
     */
    List<String> getHeadOwnerStrings(Collection<Head> heads);

    /**
     * Determine if the map of lists contains any values.
     * If there are no keys or if none of the keys return a list with any values, this will be true.
     *
     * @param listMap the map of lists
     * @return whether any heads were found
     */
    boolean isEmpty(Map<?, ? extends List<?>> listMap);
}
