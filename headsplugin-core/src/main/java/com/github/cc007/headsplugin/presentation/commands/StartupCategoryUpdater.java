package com.github.cc007.headsplugin.presentation.commands;

import com.github.cc007.headsplugin.business.services.heads.CategoryUpdater;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class StartupCategoryUpdater implements CommandLineRunner {

    private final CategoryUpdater categoryUpdater;

    @Override
    public void run(String...args) {
        log.info("Updating head categories...");
        categoryUpdater.updateCategoriesIfNecessary();
        log.info("Done updating head categories.");
    }
}