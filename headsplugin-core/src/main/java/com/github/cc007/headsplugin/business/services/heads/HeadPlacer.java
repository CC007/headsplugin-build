package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.business.domain.Head;
import com.github.cc007.headsplugin.business.services.NBTPrinter;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTTileEntity;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.Validate;
import org.apache.commons.text.StringSubstitutor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.inventory.ItemStack;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HeadPlacer {

    private final NBTPrinter nbtPrinter;

    public void placeHead(@NonNull ItemStack headItemStack, @NonNull Location location, @NonNull BlockFace rotation) {
        Validate.isTrue(headItemStack.getType().equals(Material.PLAYER_HEAD));

        val headBlock = location.getBlock();
        headBlock.setType(Material.PLAYER_HEAD);

        setRotation(headBlock, rotation);
        updateHeadBlockState(headBlock, headItemStack);
    }

    public void placeHead(@NonNull Head head, @NonNull Location location, @NonNull BlockFace rotation) {
        val headBlock = location.getBlock();
        headBlock.setType(Material.PLAYER_HEAD);

        setRotation(headBlock, rotation);
        updateHeadBlockState(headBlock, head);
    }

    public void placeWallHead(@NonNull ItemStack wallHeadItemStack, @NonNull Location location, @NonNull BlockFace facing) {
        Validate.isTrue(wallHeadItemStack.getType().equals(Material.PLAYER_HEAD));

        val wallHeadBlock = location.getBlock();
        wallHeadBlock.setType(Material.PLAYER_WALL_HEAD);

        setFacing(wallHeadBlock, facing);
        updateHeadBlockState(wallHeadBlock, wallHeadItemStack);
    }

    public void placeWallHead(@NonNull Head head, @NonNull Location location, @NonNull BlockFace facing) {
        val wallHeadBlock = location.getBlock();
        wallHeadBlock.setType(Material.PLAYER_WALL_HEAD);

        setFacing(wallHeadBlock, facing);
        updateHeadBlockState(wallHeadBlock, head);
    }

    /**
     * Set the rotation of a {@link Material#PLAYER_HEAD} block
     *
     * @param headBlock the {@link Material#PLAYER_HEAD} block
     * @param rotation  the rotation to set on the block
     */
    private void setRotation(@NonNull Block headBlock, @NonNull BlockFace rotation) {
        val headBlockData = headBlock.getBlockData();
        Validate.isInstanceOf(Rotatable.class, headBlockData);
        val rotatable = (Rotatable) headBlockData;
        rotatable.setRotation(rotation);
        headBlock.setBlockData(rotatable);
    }

    /**
     * Set the facing direction of a {@link Material#PLAYER_WALL_HEAD} block
     *
     * @param wallHeadBlock the {@link Material#PLAYER_WALL_HEAD} block
     * @param facing        the facing direction to set on the block
     */
    private void setFacing(@NonNull Block wallHeadBlock, @NonNull BlockFace facing) {
        val headBlockData = wallHeadBlock.getBlockData();
        Validate.isInstanceOf(Directional.class, headBlockData);
        val directional = (Directional) headBlockData;
        directional.setFacing(facing);
        wallHeadBlock.setBlockData(directional);
    }

    /**
     * Updates the NBT values for the player head block's state to set the skin for the head.
     *
     * @param headBlock     the player (wall) head block.
     * @param headItemStack the ItemStack containing the NBT data for the skin
     */
    private void updateHeadBlockState(@NonNull Block headBlock, @NonNull ItemStack headItemStack) {
        val headBlockState = headBlock.getState();
        val nbtTileEntity = new NBTTileEntity(headBlockState);
        val nbtItem = new NBTItem(headItemStack);

        String nbtString = nbtItem.getCompound("SkullOwner").asNBTString();
        nbtString = "{Owner:" + nbtString + "}";

        val ownerCompound = new NBTContainer(nbtString);
        nbtTileEntity.mergeCompound(ownerCompound);
    }

    /**
     * Updates the NBT values for the player head block's state to set the skin for the head.
     *
     * @param headBlock the player (wall) head block.
     * @param head      the Head containing the data for the skin
     */
    private void updateHeadBlockState(@NonNull Block headBlock, @NonNull Head head) {
        val headBlockState = headBlock.getState();
        val nbtTileEntity = new NBTTileEntity(headBlockState);

        String nbtStringTemplate = "{Owner:{Id:\"${headOwner}\",Properties:{textures:[{Value:\"${value}\"}]},Name:\"${name}\"}}";
        String nbtString = StringSubstitutor.replace(nbtStringTemplate, head.getAsMap());

        val ownerCompound = new NBTContainer(nbtString);
        nbtTileEntity.mergeCompound(ownerCompound);
    }
}
