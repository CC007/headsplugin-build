package com.github.cc007.headsplugin.presentation.startup;

import com.github.cc007.headsplugin.api.business.services.heads.CategoryUpdater;

import dev.alangomes.springspigot.util.scheduler.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class StartupCategoryUpdater implements CommandLineRunner {

    private final CategoryUpdater categoryUpdater;
    private final SchedulerService schedulerService;

    @Override
    public void run(String... args) {
        schedulerService.scheduleSyncDelayedTask(() -> {
            log.info("Updating head categories...");
            categoryUpdater.updateCategoriesIfNecessary();
            log.info("Done updating head categories.");
        }, 20);
    }
}