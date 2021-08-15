package com.github.cc007.headsplugin.api.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface HeadToItemstackMapper {
    List<ItemStack> getItemStacks(List<Head> heads);

    List<ItemStack> getItemStacks(List<Head> heads, int quantity);

    ItemStack getItemStack(Head head);

    ItemStack getItemStack(Head head, int quantity);
}
