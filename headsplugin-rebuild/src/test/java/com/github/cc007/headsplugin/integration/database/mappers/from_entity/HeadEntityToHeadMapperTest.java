package com.github.cc007.headsplugin.integration.database.mappers.from_entity;

import com.github.cc007.headsplugin.ReflectionTestUtils;
import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class HeadEntityToHeadMapperTest {

    HeadEntityToHeadMapper headEntityToHeadMapper = new HeadEntityToHeadMapper();

    @Test
    void transform() {
        // prepare
        String name = "TestName";
        UUID headOwner = UUID.randomUUID();
        String value = "TestValue";
        String databaseName1 = "TestDatabaseName1";
        String databaseName2 = "TestDatabaseName2";

        DatabaseEntity databaseEntity1 = new DatabaseEntity();
        databaseEntity1.setName(databaseName1);
        DatabaseEntity databaseEntity2 = new DatabaseEntity();
        databaseEntity2.setName(databaseName2);

        HeadEntity headEntity = new HeadEntity();
        headEntity.setName(name);
        headEntity.setHeadOwner(headOwner.toString());
        headEntity.setValue(value);
        ReflectionTestUtils.setDeclaredFieldValue(
                HeadEntity.class,
                "databases",
                headEntity,
                new HashSet<>(Arrays.asList(
                        databaseEntity1, databaseEntity2
                ))
        );

        // execute
        Head actual = headEntityToHeadMapper.transform(headEntity);

        // verify
        assertThat(actual, notNullValue());
        assertThat(actual.getName(), is(name));
        assertThat(actual.getHeadOwner(), is(headOwner));
        assertThat(actual.getValue(), is(value));
        assertThat(actual.getHeadDatabase(), containsString(databaseName1));
        assertThat(actual.getHeadDatabase(), containsString(", "));
        assertThat(actual.getHeadDatabase(), containsString(databaseName2));
    }
}