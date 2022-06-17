package com.github.cc007.headsplugin.presentation.commands.headsplugin;

import com.github.cc007.headsplugin.api.business.services.heads.CategoryUpdater;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class UpdateCategoryCompletionCandidates implements Iterable<String> {

    @Autowired
    private CategoryUpdater categoryUpdater;

    @Override
    public Iterator<String> iterator() {
        return categoryUpdater.getUpdatableCategoryNames(false).iterator();
    }
}
