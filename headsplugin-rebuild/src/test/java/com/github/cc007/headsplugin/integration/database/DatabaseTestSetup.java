package com.github.cc007.headsplugin.integration.database;

import com.github.cc007.headsplugin.dagger.HeadsPluginComponent;

import lombok.val;

import java.time.LocalDateTime;
import java.util.UUID;

public class DatabaseTestSetup {

    public static void setUpDB(HeadsPluginComponent headsPluginComponent) {
        val transaction = headsPluginComponent.transaction();
        val categoryRepository = headsPluginComponent.categoryRepository();
        val databaseRepository = headsPluginComponent.databaseRepository();
        val headRepository = headsPluginComponent.headRepository();

        transaction.runTransacted(() -> {
            val head1_1 = headRepository.manageNew();
            head1_1.setName("Head1_1");
            head1_1.setHeadOwner(UUID.randomUUID().toString());
            head1_1.setValue("Value1_1");

            val head1_2 = headRepository.manageNew();
            head1_2.setName("Head1_2");
            head1_2.setHeadOwner(UUID.randomUUID().toString());
            head1_2.setValue("Value1_2");

            val category1 = categoryRepository.manageNew();
            category1.setName("Category1");
            category1.setLastUpdated(LocalDateTime.now());
            category1.addhead(head1_1);
            category1.addhead(head1_2);


            val head2_1 = headRepository.manageNew();
            head2_1.setName("Head2_1");
            head2_1.setHeadOwner(UUID.randomUUID().toString());
            head2_1.setValue("Value2_1");

            val head2_2 = headRepository.manageNew();
            head2_2.setName("Head2_2");
            head2_2.setHeadOwner(UUID.randomUUID().toString());
            head2_2.setValue("Value2_2");

            val category2 = categoryRepository.manageNew();
            category2.setName("Category2");
            category2.setLastUpdated(LocalDateTime.now());
            category2.addhead(head2_1);
            category2.addhead(head2_2);


            val database1 = databaseRepository.manageNew();
            database1.setName("Database1");
            database1.addCategory(category1);
            database1.addCategory(category2);
            database1.addhead(head1_1);
            database1.addhead(head1_2);
            database1.addhead(head2_1);
            database1.addhead(head2_2);

            val database2 = databaseRepository.manageNew();
            database2.setName("Database2");
            database2.addCategory(category1);
            database2.addhead(head1_1);
            database2.addhead(head1_2);
        });
    }

    public static void tearDownDB(HeadsPluginComponent headsPluginComponent, boolean checkpoint) {
        val transaction = headsPluginComponent.transaction();
        val entityManager = headsPluginComponent.entityManager();

        transaction.runTransacted(() -> {
            entityManager.createNativeQuery("DROP SCHEMA PUBLIC CASCADE;").executeUpdate();
            if(checkpoint) {
                entityManager.createNativeQuery("CHECKPOINT;").executeUpdate();
            }
        });
    }
}
