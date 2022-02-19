package com.github.cc007.headsplugin.business.services;

import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTTileEntity;
import lombok.val;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

class NbtServiceTest {

    NbtService nbtService = new NbtService();

    @Test
    void getNbtItem() {
        // prepare
        val expected = mock(ItemStack.class);

        try (MockedConstruction<NBTContainer> nbtTileEntity = mockConstruction(NBTContainer.class,
                (mock, context) -> {
                    // verify
                    Object arg = context.arguments().get(0);
                    assertThat(arg, isA(ItemStack.class));
                    ItemStack actual = (ItemStack) arg;
                    assertThat(actual, is(expected));
                })) {

            // execute
            nbtService.getNbtItem(expected);
        }
    }


    @Test
    void getNbtTileEntity() {
        // prepare
        val testBlock = mock(Block.class);
        val expected = mock(BlockState.class);

        when(testBlock.getState())
                .thenReturn(expected);

        try (MockedConstruction<NBTTileEntity> nbtTileEntity = mockConstruction(NBTTileEntity.class,
                (mock, context) -> {
                    // verify
                    Object arg = context.arguments().get(0);
                    assertThat(arg, isA(BlockState.class));
                    BlockState actual = (BlockState) arg;
                    assertThat(actual, is(expected));
        })) {
            // execute
            nbtService.getNbtTileEntity(testBlock);
        }
    }

    @Test
    void getNbtContainer() {
        // prepare
        val expected = "NbtString";

        try (MockedConstruction<NBTContainer> nbtTileEntity = mockConstruction(NBTContainer.class,
                (mock, context) -> {
                    // verify
                    Object arg = context.arguments().get(0);
                    assertThat(arg, isA(String.class));
                    String actual = (String) arg;
                    assertThat(actual, is(expected));
                })) {

            // execute
            nbtService.getNbtContainer(expected);
        }
    }
}