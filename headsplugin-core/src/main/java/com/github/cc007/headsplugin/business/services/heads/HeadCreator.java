package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.business.domain.Head;

import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HeadCreator {

    /**
     * Get a List of bukkit <code>ItemStack</code> objects based on the provided
     * List of <code>Head</code> objects
     *
     * @param heads the provided List of <code>Head</code> objects
     * @return The List of bukkit <code>ItemStack</code> objects based on the
     * provided List of <code>Head</code> objects
     */
    public List<ItemStack> getItemStacks(List<Head> heads) {
        return getItemStacks(heads, 1);
    }

    /**
     * Get a List of bukkit <code>ItemStack</code> objects based on the provided
     * List of <code>Head</code> objects
     *
     * @param heads    the provided List of <code>Head</code> objects
     * @param quantity the number of heads in the <code>ItemStack</code> objects
     * @return The List of bukkit <code>ItemStack</code> objects based on the
     * provided List of <code>Head</code> objects
     */
    public List<ItemStack> getItemStacks(List<Head> heads, int quantity) {
        return heads.stream().map((head -> getItemStack(head, quantity))).collect(Collectors.toList());
    }

    /**
     * Get a bukkit <code>ItemStack</code> based on the provided
     * <code>Head</code>
     *
     * @param head the provided <code>Head</code>
     * @return The <code>ItemStack</code> based on the provided
     * <code>Head</code>
     */
    public ItemStack getItemStack(Head head) {
        return getItemStack(head, 1);
    }

    /**
     * Get a bukkit <code>ItemStack</code> based on the provided
     * <code>Head</code>
     *
     * @param head     the provided <code>Head</code>
     * @param quantity the number of heads in the <code>ItemStack</code>
     * @return The <code>ItemStack</code> based on the provided
     * <code>Head</code>
     */
    public ItemStack getItemStack(Head head, int quantity) {
        val playerHeadItemStack = new ItemStack(Material.PLAYER_HEAD, quantity);
        val headSkullMeta = Optional.ofNullable((SkullMeta) playerHeadItemStack.getItemMeta());
        headSkullMeta.ifPresent((meta) -> {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5")));
            meta.setDisplayName(head.getName());
            playerHeadItemStack.setItemMeta(meta);
        });
        val nbtItem = new NBTItem(playerHeadItemStack);
        val displayCompound = nbtItem.addCompound("display");
        displayCompound.setString("Name", "\"" + head.getName() + "\"");

        val skullOwnerCompound = nbtItem.addCompound("SkullOwner");
        skullOwnerCompound.setString("Id", head.getHeadOwner().toString());
        skullOwnerCompound.setString("Name", head.getName());
        val propertiesCompound = skullOwnerCompound.addCompound("Properties");
        val texturesCompoundList = propertiesCompound.getCompoundList("textures");
        val textureListCompound = texturesCompoundList.addCompound();
        textureListCompound.setString("Value", head.getValue());

        return nbtItem.getItem();
    }
}
