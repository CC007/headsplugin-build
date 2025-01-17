package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.HeadToItemstackMapper;
import com.github.cc007.headsplugin.api.business.services.heads.utils.HeadUtils;
import com.github.cc007.headsplugin.business.services.OwnerProfileService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.extern.log4j.Log4j2;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@ExtensionMethod(HeadToItemstackMapperImpl.class)
public class HeadToItemstackMapperImpl implements HeadToItemstackMapper {

    private static PlayerProfile cachedPlayerProfile;
    private final HeadUtils headUtils;
    private final OwnerProfileService ownerProfileService;

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
        final var playerHeadItemStack = new ItemStack(Material.PLAYER_HEAD, quantity);
        initSkullMeta(playerHeadItemStack, head);
        Optional.ofNullable((SkullMeta) playerHeadItemStack.getItemMeta())
                .map(SkullMeta::getOwnerProfile)
                .map(PlayerProfile::getTextures)
                .map(PlayerTextures::getSkin)
                .ifPresentOrElse(skin -> log.debug("Skin url: " + skin), () -> log.debug("No skin URL found"));
        return playerHeadItemStack;
    }

    private void initSkullMeta(@NonNull ItemStack playerHeadItemStack, @NonNull Head head) {
        final var headSkullMeta = Optional.ofNullable((SkullMeta) playerHeadItemStack.getItemMeta());
        headSkullMeta.ifPresentOrElse(meta -> {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer("Notch"));
            meta.setDisplayName(head.getName());
            meta.setOwnerProfile(ownerProfileService.createOwnerProfile(head));
            playerHeadItemStack.setItemMeta(meta);
        }, () -> {
            log.warn("Couldn't find player skull meta.");
        });
    }
}
