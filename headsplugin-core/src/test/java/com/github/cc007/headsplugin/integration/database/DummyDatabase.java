package com.github.cc007.headsplugin.integration.database;

import com.github.cc007.headsplugin.dagger.DaggerHeadsPluginComponent;
import com.github.cc007.headsplugin.dagger.HeadsPluginComponent;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.mockito.MockedStatic;

import java.io.File;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.function.Consumer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class DummyDatabase {

    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(DummyDatabase.class);
    private static boolean up = false;
    private static HeadsPluginComponent headsPluginComponent;

    static {
        headsPluginComponent = DaggerHeadsPluginComponent.builder().mainThread(Thread.currentThread()).build();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> tearDownDB(headsPluginComponent, true)));
        System.out.println(LocalTime.now().format(DateTimeFormatter.ISO_TIME) + ": Database teardown hook added");
    }

    /**
     * Set up the database if it isn't already set up.
     * <p>
     * This setup will create dummy data for heads, categories, databases, tags and searches that will be used in tests.
     *
     * @param headsPluginComponent the dagger component to be used
     */
    public static void setUpDB(HeadsPluginComponent headsPluginComponent) {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            setupBukkitExpectations(bukkit);
            if (!isUp()) {
                System.out.println(LocalTime.now().format(DateTimeFormatter.ISO_TIME) + ": Database setup started...");
                final var transaction = headsPluginComponent.transaction();
                final var categoryRepository = headsPluginComponent.categoryRepository();
                final var databaseRepository = headsPluginComponent.databaseRepository();
                final var searchRepository = headsPluginComponent.searchRepository();
                final var tagRepository = headsPluginComponent.tagRepository();
                final var headRepository = headsPluginComponent.headRepository();

                transaction.runTransacted(() -> {
                    final var head1_1 = headRepository.manageNew();
                    head1_1.setName("Head1_1");
                    head1_1.setHeadOwner(UUID.randomUUID().toString());
                    head1_1.setValue("Value1_1");

                    final var head1_2 = headRepository.manageNew();
                    head1_2.setName("Head1_2");
                    head1_2.setHeadOwner(UUID.randomUUID().toString());
                    head1_2.setValue("Value1_2");

                    final var category1 = categoryRepository.manageNew();
                    category1.setName("Category1");
                    category1.setLastUpdated(LocalDateTime.now());
                    category1.addhead(head1_1);
                    category1.addhead(head1_2);

                    final var head2_1 = headRepository.manageNew();
                    head2_1.setName("Head2_1");
                    head2_1.setHeadOwner(UUID.randomUUID().toString());
                    head2_1.setValue("Value2_1");

                    final var head2_2 = headRepository.manageNew();
                    head2_2.setName("Head2_2");
                    head2_2.setHeadOwner(UUID.randomUUID().toString());
                    head2_2.setValue("Value2_2");

                    final var category2 = categoryRepository.manageNew();
                    category2.setName("Category2");
                    category2.setLastUpdated(LocalDateTime.now());
                    category2.addhead(head2_1);
                    category2.addhead(head2_2);

                    final var tag = tagRepository.manageNew();
                    tag.setName("Tag1");
                    tag.addhead(head1_1);
                    tag.addhead(head2_2);

                    final var database1 = databaseRepository.manageNew();
                    database1.setName("Database1");
                    database1.addCategory(category1);
                    database1.addCategory(category2);
                    database1.addTag(tag);
                    database1.addhead(head1_1);
                    database1.addhead(head1_2);
                    database1.addhead(head2_1);
                    database1.addhead(head2_2);

                    final var database2 = databaseRepository.manageNew();
                    database2.setName("Database2");
                    database2.addCategory(category1);
                    database2.addhead(head1_1);
                    database2.addhead(head1_2);

                    final var search = searchRepository.manageNew();
                    search.setSearchTerm("Search1");
                    search.setLastUpdated(LocalDateTime.now());
                    search.addhead(head1_2);
                    search.addhead(head2_2);
                    search.incrementSearchCount();
                    search.incrementSearchCount();
                });
                up = true;
                System.out.println(LocalTime.now().format(DateTimeFormatter.ISO_TIME) + ": Database setup finished");
            }
        }
    }

    /**
     * Tear down the database that is set up for the tests
     *
     * @param headsPluginComponent the dagger component to be used
     * @param checkpoint           if true, call the checkpoint sql statement to flush the database log.
     */
    public static void tearDownDB(HeadsPluginComponent headsPluginComponent, boolean checkpoint) {
        if (isUp()) {
            System.out.println(LocalTime.now().format(DateTimeFormatter.ISO_TIME) + ": Database teardown started...");
            final var transaction = headsPluginComponent.transaction();
            final var entityManager = headsPluginComponent.entityManager();

            transaction.runTransacted(() -> {
                entityManager.createNativeQuery("DROP SCHEMA \"PUBLIC\" CASCADE;").executeUpdate();
                if (checkpoint) {
                    entityManager.createNativeQuery("CHECKPOINT;").executeUpdate();
                }
            });
            up = false;
            System.out.println(LocalTime.now().format(DateTimeFormatter.ISO_TIME) + ": Database teardown finished");
        }
    }

    /**
     * Set up the database again cleanly so that any modifications from after the previous setup are reversed
     *
     * @param headsPluginComponent the dagger component to be used
     * @param checkpoint           if true, call the checkpoint sql statement to flush the database log.
     */
    public static void cleanUpDB(HeadsPluginComponent headsPluginComponent, boolean checkpoint) {
        tearDownDB(headsPluginComponent, checkpoint);
        setUpDB(headsPluginComponent);
    }

    /**
     * This will run the provided consumer after the database is set up.
     * The HeadsPluginComponent given to this consumer is managed by this class.
     * <p>
     * If it's needed for the test class to provide this dagger component itself,
     * then the setUpDB method can be used manually.
     *
     * @param consumer the consumer to be executed after the database is set up.
     */
    public static void runWithDB(Consumer<HeadsPluginComponent> consumer) {
        setUpDB(headsPluginComponent);
        consumer.accept(headsPluginComponent);
    }

    /**
     * This will run the provided consumer after the database is set up.
     * The HeadsPluginComponent given to this consumer is managed by this class.
     * <p>
     * If it's needed for the test class to provide this dagger component itself,
     * then the setUpDB/tearDownDB/cleanUpDB methods can be used manually.
     *
     * @param consumer the consumer to be executed after the database is set up.
     */
    public static void runDirtyWithDB(boolean checkpoint, Consumer<HeadsPluginComponent> consumer) {
        setUpDB(headsPluginComponent);
        consumer.accept(headsPluginComponent);
        cleanUpDB(headsPluginComponent, checkpoint);
    }

    /**
     * The repositories can be dependent on the config, which requires {@link Plugin#getResource(String)}
     * to be available to be called. To get access to this plugin, Bukkit needs to provide a server from which to
     * retrieve this plugin.
     *
     * @param bukkit the static mock of the {@link Bukkit} class
     */
    private static void setupBukkitExpectations(MockedStatic<Bukkit> bukkit) {
        final var server = mock(Server.class);
        final var pluginManager = mock(PluginManager.class);
        final var plugin = mock(Plugin.class);
        final var dataFolder = new File("src" + File.separator + "main" + File.separator + "resources");

        bukkit.when(Bukkit::getServer)
                .thenReturn(server);
        when(server.getPluginManager())
                .thenReturn(pluginManager);
        when(pluginManager.getPlugin("HeadsPluginAPI"))
                .thenReturn(plugin);
        when(plugin.isEnabled())
                .thenReturn(true);
        when(plugin.getDataFolder())
                .thenReturn(dataFolder);
    }

    public static boolean isUp() {
        return DummyDatabase.up;
    }
}
