package com.github.cc007.headsplugin.business.services;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

public class NbtService {

    public NBTItem getNbtItem(ItemStack playerHeadItemStack) {
        return new NBTItem(playerHeadItemStack);
    }
}