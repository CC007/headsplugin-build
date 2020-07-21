package com.github.cc007.headsplugin.presentation.startup;

import com.github.cc007.headsplugin.api.business.services.heads.CategoryUpdater;

import dev.alangomes.springspigot.util.scheduler.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@Log4j2
@RequiredArgsConstructor
public class StartupCategoryUpdater implements CommandLineRunner {

    private final CategoryUpdater categoryUpdater;
    private final SchedulerService schedulerService;

    /**
     * The delay in seconds between category updates
     */
    @Value("${headsplugin.categories.update.delay:10}")
    private int delayBetweenCategoryUpdates = 10;

    @Override
    public void run(String... args) {
        Collection<String> updatableCategoryNames = categoryUpdater.getUpdatableCategoryNames(true);
        log.info("Updating head categories (" + String.join(", ", updatableCategoryNames) + ")...");
        int delay = 0;
        for (String updatableCategoryName : updatableCategoryNames) {
            schedulerService.scheduleSyncDelayedTask(() -> {
                categoryUpdater.updateCategory(updatableCategoryName);
                log.info(updatableCategoryName + " category is now updated.");
            }, delay);
            delay += 20 * delayBetweenCategoryUpdates;
        }
        log.info("All categories updates are now scheduled (with " + delayBetweenCategoryUpdates + " seconds between each category).");
    }
}