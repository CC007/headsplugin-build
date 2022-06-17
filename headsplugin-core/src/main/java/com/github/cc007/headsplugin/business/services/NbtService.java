package com.github.cc007.headsplugin.business.services;

import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTTileEntity;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class NbtService {

    public NBTItem getNbtItem(ItemStack playerHeadItemStack) {
        return new NBTItem(playerHeadItemStack);
    }

    public NBTTileEntity getNbtTileEntity(Block playerHeadBlock) {
        return new NBTTileEntity(playerHeadBlock.getState());
    }

    public NBTContainer getNbtContainer(String nbtString) {
        return new NBTContainer(nbtString);
    }
}