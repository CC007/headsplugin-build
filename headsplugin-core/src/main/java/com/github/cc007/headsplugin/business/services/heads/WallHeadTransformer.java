package com.github.cc007.headsplugin.business.services.heads;

import lombok.NonNull;
import lombok.val;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.springframework.stereotype.Component;

/**
 * Transforms a {@link Material#PLAYER_HEAD} into a {@link Material#PLAYER_WALL_HEAD}
 */
@Component
public class WallHeadTransformer implements Transformer<ItemStack, ItemStack> {

    @Override
    public ItemStack transform(@NonNull ItemStack headItemStack) {
        Validate.isTrue(headItemStack.getType().equals(Material.PLAYER_HEAD));
        val wallHeadItemStack = headItemStack.clone();
        wallHeadItemStack.setType(Material.PLAYER_WALL_HEAD);
        return wallHeadItemStack;
    }
}
