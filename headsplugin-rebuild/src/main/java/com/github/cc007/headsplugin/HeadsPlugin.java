package com.github.cc007.headsplugin;

import com.github.cc007.headsplugin.api.HeadsPluginApi;
import com.github.cc007.headsplugin.api.HeadsPluginServices;
import com.github.cc007.headsplugin.dagger.DaggerHeadsPluginComponent;
import com.github.cc007.headsplugin.dagger.HeadsPluginComponent;
import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;
import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.repositories.CategoryRepository;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;
import com.github.cc007.headsplugin.integration.database.repositories.HeadRepository;
import com.github.cc007.headsplugin.integration.database.transaction.Transaction;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.openjpa.persistence.EntityExistsException;
import org.bukkit.plugin.java.JavaPlugin;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Log4j2
public class HeadsPlugin extends JavaPlugin implements HeadsPluginApi {

    private HeadsPluginComponent headsPluginComponent;
    private ClassLoader defaultClassLoader;

    /**
     * Gets the instance of the HeadsPlugin
     *
     * @return Optional of the HeadsPlugin plugin
     */
    public static Optional<HeadsPlugin> getPlugin() {
        return HeadsPluginApi.getPlugin("HeadsPluginAPI")
                .filter(plugin -> plugin instanceof HeadsPlugin)
                .map(plugin -> (HeadsPlugin) plugin);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        defaultClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClassLoader());
        headsPluginComponent = DaggerHeadsPluginComponent.create();

        //StartupCategoryUpdater startupCategoryUpdater = headsPluginComponent.startupCategoryUpdater();
        //startupCategoryUpdater.update();

        experiments();
    }

    @Override
    public void onDisable() {
        shutdownDatabase();
        Thread.currentThread().setContextClassLoader(defaultClassLoader);
    }

    @Override
    public HeadsPluginComponent getHeadsPluginServices() {
        return headsPluginComponent;
    }

    private void shutdownDatabase() {
        EntityManager entityManager = getHeadsPluginServices().entityManager();
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("CHECKPOINT;").executeUpdate();
        entityManager.createNativeQuery("SHUTDOWN;").executeUpdate();
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    private void experiments() {
        // how other plugins use this plugin's API
        HeadsPluginApi headsPluginApi = HeadsPluginApi.getPlugin().orElseThrow(IllegalStateException::new);
        HeadsPluginServices headsPluginServices = headsPluginApi.getHeadsPluginServices();

        CategoryRepository categoryRepository = headsPluginServices.categoryRepository();
        DatabaseRepository databaseRepository = headsPluginServices.databaseRepository();
        HeadRepository headRepository = headsPluginServices.headRepository();
        EntityManager entityManager = headsPluginServices.entityManager();


        String headOwner1_2 = UUID.randomUUID().toString();
        String headOwner2_1 = UUID.randomUUID().toString();
        setupDB(headsPluginServices, headOwner1_2, headOwner2_1);

        log.info("Find database by name: Database1");
        databaseRepository.findByName("Database1").ifPresent(database -> {
            entityManager.refresh(database);
            log.info(" Database found: " + prettyPrint(database.toString(), true));
            database.getCategories()
                    .forEach(category ->
                            log.info(" Category named \"" + category.getName() + "\" " +
                                    "with number of databases: " + category.getDatabases().size()));
        });
        log.info("Find database by name: Database2");
        databaseRepository.findByName("Database2").ifPresent(database -> {
            entityManager.refresh(database);
            log.info(" Database found: " + prettyPrint(database.toString(), true));
            database.getCategories()
                    .forEach(category ->
                            log.info(" Category named \"" + category.getName() + "\" " +
                                    "with number of databases: " + category.getDatabases().size()));
        });

        log.info("Find category by name: Category1");
        categoryRepository.findByName("Category1")
                .ifPresent(category -> log.info(" Category found: " + prettyPrint(category.toString(), true)));

        log.info("Find head by headOwner: " + headOwner2_1);
        headRepository.findByHeadOwner(headOwner2_1)
                .ifPresent(head -> log.info(" Head found: " + prettyPrint(head.toString(), true)));

        log.info("Find head by headOwner in: [" + headOwner2_1 + ", " + headOwner1_2 + "]");
        headRepository.findAllByHeadOwnerIn(Arrays.asList(headOwner2_1, headOwner1_2))
                .forEach(head -> log.info(" Head found: " + prettyPrint(head.toString(), true)));

        log.info("Find head by name ignore case containing: head2");
        headRepository.findAllByNameIgnoreCaseContaining("head2")
                .forEach(head -> log.info(" Head found: " + prettyPrint(head.toString(), true)));

        log.info("Find head by database name (Database2) and headOwner in: [" + headOwner2_1 + ", " + headOwner1_2 + ", <random UUID>]");
        headRepository.findAllByDatabases_NameAndHeadOwnerIn(
                "Database2",
                Arrays.asList(headOwner2_1, headOwner1_2, UUID.randomUUID().toString())
        ).forEach(head -> log.info(" Head found: " + prettyPrint(head.toString(), true)));

        log.info("Find headOwner by headOwner in: [" + headOwner2_1 + ", " + headOwner1_2 + ", <random UUID>]");
        headRepository.findAllHeadOwnersByHeadOwnerIn(Arrays.asList(headOwner2_1, headOwner1_2, UUID.randomUUID().toString()))
                .forEach(headOwner -> log.info(" headOwner found: " + headOwner));
    }

    private void setupDB(HeadsPluginServices headsPluginServices, String headOwner1_2, String headOwner2_1) {
        Transaction transaction = headsPluginServices.transaction();
        CategoryRepository categoryRepository = headsPluginServices.categoryRepository();
        DatabaseRepository databaseRepository = headsPluginServices.databaseRepository();
        HeadRepository headRepository = headsPluginServices.headRepository();

        try {
            transaction.begin();

            HeadEntity head1_1 = headRepository.manageNew();
            head1_1.setName("Head1_1");
            head1_1.setHeadOwner(UUID.randomUUID().toString());
            head1_1.setValue("Value1_1");

            HeadEntity head1_2 = headRepository.manageNew();
            head1_2.setName("Head1_2");
            head1_2.setHeadOwner(headOwner1_2);
            head1_2.setValue("Value1_2");

            CategoryEntity category1 = categoryRepository.manageNew();
            category1.setName("Category1");
            category1.setLastUpdated(LocalDateTime.now());
            category1.addhead(head1_1);
            category1.addhead(head1_2);


            HeadEntity head2_1 = headRepository.manageNew();
            head2_1.setName("Head2_1");
            head2_1.setHeadOwner(headOwner2_1);
            head2_1.setValue("Value2_1");

            HeadEntity head2_2 = headRepository.manageNew();
            head2_2.setName("Head2_2");
            head2_2.setHeadOwner(UUID.randomUUID().toString());
            head2_2.setValue("Value2_2");

            CategoryEntity category2 = categoryRepository.manageNew();
            category2.setName("Category2");
            category2.setLastUpdated(LocalDateTime.now());
            category2.addhead(head2_1);
            category2.addhead(head2_2);


            DatabaseEntity database1 = databaseRepository.manageNew();
            database1.setName("Database1");
            database1.addCategory(category1);
            database1.addCategory(category2);
            database1.addhead(head1_1);
            database1.addhead(head1_2);
            database1.addhead(head2_1);
            database1.addhead(head2_2);

            DatabaseEntity database2 = databaseRepository.manageNew();
            database2.setName("Database2");
            database2.addCategory(category1);
            database2.addhead(head1_1);
            database2.addhead(head1_2);

            transaction.commit(true);
        } catch (RollbackException e) {
            if (e.getCause() instanceof EntityExistsException || e.getCause().getCause() instanceof EntityExistsException) {
                log.warn("Entities were already added to the database");
            } else {
                throw e;
            }
        }
    }

    private String prettyPrint(String string, boolean newLineBeforeClosingBracket) {
        StringBuilder sb = new StringBuilder().append('\n');
        StringReader sr = new StringReader(string);
        prettyPrint(sr, sb, 0, newLineBeforeClosingBracket);
        return sb.toString();
    }

    @SneakyThrows(IOException.class)
    private int prettyPrint(StringReader sr, StringBuilder sb, int indent, boolean newLineBeforeClosingBracket) {
        int character = sr.read();
        while (character != -1) {
            if (character == ']' || character == ')') {
                return character;
            }
            if (character == '[' || character == '(') {
                sb.append((char) character);
                println(sb, indent + 2);

                character = prettyPrint(sr, sb, indent + 2, newLineBeforeClosingBracket);
                if (newLineBeforeClosingBracket) {
                    println(sb, indent);
                }
            }
            sb.append((char) character);

            if (character == ',') {
                println(sb, indent);
                // skip space
                sr.read();
            }
            character = sr.read();
        }
        return character;
    }

    private void println(StringBuilder sb, int indent) {
        sb.append('\n')
                .append(String.join("", Collections.nCopies(indent, " ")));
    }
}
