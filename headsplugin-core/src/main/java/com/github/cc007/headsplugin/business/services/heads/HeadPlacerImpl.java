package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.HeadPlacer;
import com.github.cc007.headsplugin.api.business.services.heads.utils.HeadUtils;
import com.github.cc007.headsplugin.business.services.NbtService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.apache.commons.text.StringSubstitutor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class HeadPlacerImpl implements HeadPlacer {

    private final HeadUtils headUtils;
    private final NbtService nbtService;

    @Override
    public void placeHead(@NonNull ItemStack headItemStack, @NonNull Location location, @NonNull BlockFace rotation) {
        Validate.isTrue(
                headItemStack.getType().equals(Material.PLAYER_HEAD),
                "The Material of the provided ItemStack is not equal to PLAYER_HEAD"
        );

        final var headBlock = location.getBlock();
        headBlock.setType(Material.PLAYER_HEAD);

        setRotation(headBlock, rotation);
        updateHeadBlockState(headBlock, getNbtString(headItemStack));
    }

    @Override
    public void placeHead(@NonNull Head head, @NonNull Location location, @NonNull BlockFace rotation) {
        final var headBlock = location.getBlock();
        headBlock.setType(Material.PLAYER_HEAD);

        setRotation(headBlock, rotation);
        updateHeadBlockState(headBlock, getNbtString(head));
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
        updateHeadBlockState(wallHeadBlock, getNbtString(wallHeadItemStack));
    }

    @Override
    public void placeWallHead(@NonNull Head head, @NonNull Location location, @NonNull BlockFace facing) {
        final var wallHeadBlock = location.getBlock();
        wallHeadBlock.setType(Material.PLAYER_WALL_HEAD);

        setFacing(wallHeadBlock, facing);
        updateHeadBlockState(wallHeadBlock, getNbtString(head));
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
     * Updates the NBT values for the player head block's state to set the skin for the head.
     *
     * @param headBlock the player (wall) head block.
     * @param nbtString the NBT string containing the skin data for the head.
     */
    private void updateHeadBlockState(@NonNull Block headBlock, String nbtString) {
        nbtService.getNbtTileEntity(headBlock)
                .mergeCompound(nbtService.getNbtContainer(nbtString));
    }

    /**
     * Get the NBT string for the SkullOwner property of the block.
     * This string contains the information for the skin.
     *
     * @param headItemStack the ItemStack containing the NBT data for the skin
     * @return the NBT String
     */
    private String getNbtString(ItemStack headItemStack) {
        final var nbtItem = nbtService.getNbtItem(headItemStack);
        return "{SkullOwner:" + nbtItem.getCompound("SkullOwner").toString() + "}";
    }

    /**
     * Get the NBT string for the SkullOwner property of the block.
     * This string contains the information for the skin.
     *
     * @param head the Head containing the data for the skin
     * @return the NBT String
     */
    private String getNbtString(Head head) {
        final var headMap = head.getAsMap();
        headMap.put("headOwner", getAsIntArrayString(head.getHeadOwner()));

        return StringSubstitutor.replace(
                "{SkullOwner:{Id:${headOwner},Properties:{textures:[{Value:\"${value}\"}]},Name:\"${name}\"}}",
                headMap
        );
    }

    /**
     * Get the head owner UUID as an NBT int array string.
     *
     * @param headOwner the UUID of the head owner
     * @return the NBT int array string for that UUID
     */
    private String getAsIntArrayString(UUID headOwner) {
        final var headOwnerIntArray = headUtils.getIntArrayFromUuid(headOwner);
        String headOwnerIntArrayString = Arrays.stream(headOwnerIntArray)
                .boxed()
                .map(Object::toString)
                .collect(Collectors.joining(","));
        return "[I;" + headOwnerIntArrayString + "]";
    }
}
