package com.github.cc007.headsplugin.api.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

public interface HeadPlacer {
    void placeHead(@NonNull ItemStack headItemStack, @NonNull Location location, @NonNull BlockFace rotation);

    void placeHead(@NonNull Head head, @NonNull Location location, @NonNull BlockFace rotation);

    void placeWallHead(@NonNull ItemStack wallHeadItemStack, @NonNull Location location, @NonNull BlockFace facing);

    void placeWallHead(@NonNull Head head, @NonNull Location location, @NonNull BlockFace facing);
}
