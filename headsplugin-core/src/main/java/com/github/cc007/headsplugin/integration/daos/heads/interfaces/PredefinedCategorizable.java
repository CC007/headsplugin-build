package com.github.cc007.headsplugin.integration.daos.heads.interfaces;

import java.util.ArrayList;
import java.util.List;

public interface PredefinedCategorizable extends Categorizable {

    /**
     * Return an empty list, because the heads database doesn't have custom search capabilities in its api.
     *
     * @return an empty ArrayList
     */
    @Override
    default List<String> getCustomCategoryNames() {
        return new ArrayList<>();
    }
}
