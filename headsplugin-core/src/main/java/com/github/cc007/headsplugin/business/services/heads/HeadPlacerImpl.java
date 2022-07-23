package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.HeadPlacer;
import com.github.cc007.headsplugin.api.business.services.heads.utils.HeadUtils;
import com.github.cc007.headsplugin.business.services.OwnerProfileService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.util.Objects;

@RequiredArgsConstructor
public class HeadPlacerImpl implements HeadPlacer {

    private final HeadUtils headUtils;
    private final OwnerProfileService ownerProfileService;

    @Override
    public void placeHead(@NonNull ItemStack headItemStack, @NonNull Location location, @NonNull BlockFace rotation) {
        Validate.isTrue(
                headItemStack.getType().equals(Material.PLAYER_HEAD),
                "The Material of the provided ItemStack is not equal to PLAYER_HEAD"
        );

        final var headBlock = location.getBlock();
        headBlock.setType(Material.PLAYER_HEAD);

        setRotation(headBlock, rotation);
        setOwnerProfile(headBlock, mapOwnerProfile(headItemStack));
    }

    @Override
    public void placeHead(@NonNull Head head, @NonNull Location location, @NonNull BlockFace rotation) {
        final var headBlock = location.getBlock();
        headBlock.setType(Material.PLAYER_HEAD);

        setRotation(headBlock, rotation);
        setOwnerProfile(headBlock, mapOwnerProfile(head));
    }

    @Override
    public void placeWallHead(@NonNull ItemStack wallHeadItemStack, @NonNull Location location, @NonNull BlockFace facing) {
        Validate.isTrue(
                wallHeadItemStack.getType().equals(Material.PLAYER_HEAD),
                "The Material of the provided ItemStack is not equal to PLAYER_HEAD"
        );

        final var wallHeadBlock = location.getBlock();
        wallHeadBlock.setType(Material.PLAYER_WALL_HEAD);

        setFacing(wallHeadBlock, facing);
        setOwnerProfile(wallHeadBlock, mapOwnerProfile(wallHeadItemStack));
    }

    @Override
    public void placeWallHead(@NonNull Head head, @NonNull Location location, @NonNull BlockFace facing) {
        final var wallHeadBlock = location.getBlock();
        wallHeadBlock.setType(Material.PLAYER_WALL_HEAD);

        setFacing(wallHeadBlock, facing);
        setOwnerProfile(wallHeadBlock, mapOwnerProfile(head));
    }

    /**
     * Set the rotation of a {@link Material#PLAYER_HEAD} block
     *
     * @param headBlock the {@link Material#PLAYER_HEAD} block
     * @param rotation  the rotation to set on the block
     */
    private void setRotation(@NonNull Block headBlock, @NonNull BlockFace rotation) {
        final var headBlockData = headBlock.getBlockData();
        if (headBlockData instanceof Rotatable rotatable) {
            rotatable.setRotation(rotation);
            headBlock.setBlockData(rotatable);
        }
    }

    /**
     * Set the facing direction of a {@link Material#PLAYER_WALL_HEAD} block
     *
     * @param wallHeadBlock the {@link Material#PLAYER_WALL_HEAD} block
     * @param facing        the facing direction to set on the block
     */
    private void setFacing(@NonNull Block wallHeadBlock, @NonNull BlockFace facing) {
        final var headBlockData = wallHeadBlock.getBlockData();
        if (headBlockData instanceof Directional directional) {
            directional.setFacing(facing);
            wallHeadBlock.setBlockData(directional);
        }
    }

    /**
     * Get the head owner's {@link PlayerProfile} based on an item stack of a {@link Material#PLAYER_HEAD}
     *
     * @param itemStack the item stack of a {@link Material#PLAYER_HEAD}
     * @return the player profile of the head owner
     */
    private PlayerProfile mapOwnerProfile(@NonNull ItemStack itemStack) {
        final var itemMeta = Objects.requireNonNull(
                itemStack.getItemMeta(),
                "This method should only be called with an item stack that represents a stack of player heads!"
        );
        if (!(itemMeta instanceof SkullMeta skullMeta)) {
            throw new IllegalArgumentException("This method should only be called with an item stack that represents a stack of player heads!");
        }
        return Objects.requireNonNull(
                skullMeta.getOwnerProfile(),
                "This method should only be called with an item stack that represents a stack of player heads that have a skin defined through its owner profile!"
        );
    }

    /**
     * Get the head owner's {@link PlayerProfile} based on a given head
     *
     * @param head the head with the name, uuid and texture link
     * @return the player profile of the head owner
     */
    private PlayerProfile mapOwnerProfile(@NonNull Head head) {
        return ownerProfileService.createOwnerProfile(head);
    }

    /**
     * Updates the NBT values for the player head block's state to set the skin for the head.
     *
     * @param headBlock the player (wall) head block.
     * @param ownerProfile the {@link PlayerProfile} of the head owner.
     */
    private void setOwnerProfile(@NonNull Block headBlock, PlayerProfile ownerProfile) {
        final var headBlockState = headBlock.getState();
        if (headBlockState instanceof Skull skullBlock) {
            skullBlock.setOwnerProfile(ownerProfile);
            skullBlock.update();
        }
    }
}
