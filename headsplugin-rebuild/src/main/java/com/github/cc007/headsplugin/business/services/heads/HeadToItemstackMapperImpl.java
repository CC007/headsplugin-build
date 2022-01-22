package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.HeadToItemstackMapper;
import com.github.cc007.headsplugin.api.business.services.heads.utils.HeadUtils;
import com.github.cc007.headsplugin.business.services.NbtService;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@ExtensionMethod(HeadToItemstackMapperImpl.class)
public class HeadToItemstackMapperImpl implements HeadToItemstackMapper {

    private final HeadUtils headUtils;
    private final NbtService nbtService;

    /**
     * Get a List of bukkit <code>ItemStack</code> objects based on the provided
     * List of <code>Head</code> objects
     *
     * @param heads the provided List of <code>Head</code> objects
     * @return The List of bukkit <code>ItemStack</code> objects based on the
     * provided List of <code>Head</code> objects
     */
    @Override
    public List<ItemStack> getItemStacks(@NonNull List<Head> heads) {
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
    @Override
    public List<ItemStack> getItemStacks(@NonNull List<Head> heads, int quantity) {
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
    @Override
    public ItemStack getItemStack(@NonNull Head head) {
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
    @Override
    public ItemStack getItemStack(@NonNull Head head, int quantity) {
        val playerHeadItemStack = new ItemStack(Material.PLAYER_HEAD, quantity);
        initSkullMeta(playerHeadItemStack, head.getName());
        return initNbtData(head, playerHeadItemStack);
    }

    private void initSkullMeta(ItemStack playerHeadItemStack, String displayName) {
        val headSkullMeta = Optional.ofNullable((SkullMeta) playerHeadItemStack.getItemMeta());
        headSkullMeta.ifPresent((meta) -> {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5")));
            meta.setDisplayName(displayName);
            playerHeadItemStack.setItemMeta(meta);
        });
    }

    private ItemStack initNbtData(Head head, ItemStack playerHeadItemStack) {
        val nbtItem = nbtService.getNbtItem(playerHeadItemStack);
        setDisplayName(nbtItem, head.getName());
        setSkullOwner(nbtItem, head);
        return nbtItem.getItem();
    }

    private void setDisplayName(NBTItem nbtItem, String displayName) {
        val displayCompound = nbtItem.addCompound("display");
        displayCompound.setString("Name", "\"" + displayName + "\"");
    }

    private void setSkullOwner(NBTItem nbtItem, Head head) {
        val idIntArray = headUtils.getIntArrayFromUuid(head.getHeadOwner());
        val skullOwnerCompound = nbtItem.addCompound("SkullOwner");
        skullOwnerCompound.setIntArray("Id", idIntArray);
        skullOwnerCompound.setString("Name", head.getName());
        setTextureValueProperty(skullOwnerCompound, head.getValue());
    }

    private void setTextureValueProperty(NBTCompound skullOwnerCompound, String headValue) {
        val propertiesCompound = skullOwnerCompound.addCompound("Properties");
        val texturesCompoundList = propertiesCompound.getCompoundList("textures");
        val textureListCompound = texturesCompoundList.addCompound();
        textureListCompound.setString("Value", headValue);
    }

}
