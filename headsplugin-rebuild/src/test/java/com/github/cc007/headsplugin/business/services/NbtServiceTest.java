package com.github.cc007.headsplugin.business.services;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class NbtServiceTest {

    @Test
    void getNbtItem() {
        // prepare
        NbtService nbtService = new NbtService();
        ItemStack itemStack = mock(ItemStack.class);
        ItemStack itemStackClone = mock(ItemStack.class);
        doReturn(itemStackClone)
                .when(itemStack).clone();

        // execute
        NBTItem actual = nbtService.getNbtItem(itemStack);

        // verify
        assertThat(actual.getItem(), sameInstance(itemStackClone));
    }
}