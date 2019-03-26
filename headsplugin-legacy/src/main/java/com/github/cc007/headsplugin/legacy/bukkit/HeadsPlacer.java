/* 
 * The MIT License
 *
 * Copyright 2015 Rik Schaaf aka CC007 <http://coolcat007.nl/>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.cc007.headsplugin.legacy.bukkit;

import com.github.cc007.headsplugin.legacy.utils.MinecraftVersion;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class HeadsPlacer {

    /**
     * Place a head at a specified location in a specified world with a rotation
     *
     * @param item the head
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * @param rotation the rotation BlockFace object
     * @param w the world
     * @param log the logger to be used
     */
    public static void placeHead(ItemStack item, int x, int y, int z, BlockFace rotation, World w, Logger log) {
        placeHead(item, x, y, z, BlockFace.SELF, rotation, w, log);
    }

    /**
     * Place a head at a specified location in a specified world with a
     * rotation. It also gives the option to specify an attachment direction of
     * the head: - SELF for on the ground - NORTH, EAST, SOUTH and WEST for to
     * the sides of a block
     *
     * @param item the head
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * @param attachmentDirection the an attachment direction of the head
     * @param rotation the rotation BlockFace object
     * @param w the world
     * @param log the logger to be used
     */
    public static void placeHead(ItemStack item, int x, int y, int z, String attachmentDirection, BlockFace rotation, World w, Logger log) {
        //method itself
        BlockFace ad;
        switch (attachmentDirection.toUpperCase()) {
            default:
            case "UP":
            case "DOWN":
            case "SELF":
                ad = BlockFace.SELF;
                break;
            case "NORTH":
                ad = BlockFace.NORTH;
                break;
            case "EAST":
                ad = BlockFace.EAST;
                break;
            case "SOUTH":
                ad = BlockFace.SOUTH;
                break;
            case "WEST":
                ad = BlockFace.WEST;
                break;
        }

        placeHead(item, x, y, z, ad, rotation, w, log);
    }

    /**
     * Place a head at a specified location in a specified world with a
     * rotation. It also gives the option to specify an attachment direction of
     * the head: - SELF for on the ground - NORTH, EAST, SOUTH and WEST for to
     * the sides of a block
     *
     * @param item the head
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * @param attachmentDirection the an attachment direction of the head
     * @param rotation the rotation BlockFace object
     * @param w the world
     * @param log the logger to be used
     */
    public static void placeHead(ItemStack item, int x, int y, int z, BlockFace attachmentDirection, BlockFace rotation, World w, Logger log) {
        // get minecraft version
        MinecraftVersion version = new MinecraftVersion();

        String skullMaterialName;
        if (version.getMinor() < 13) {
            skullMaterialName = "SKULL_ITEM";
        } else if (isWallHead(attachmentDirection)) {
            skullMaterialName = "PLAYER_WALL_HEAD";
        } else {
            skullMaterialName = "PLAYER_HEAD";
        }
        Material skullMaterial;
        try {
            Class<?> obMaterialClass = Material.class;
            skullMaterial = (Material) obMaterialClass.getDeclaredField(skullMaterialName).get(null);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            skullMaterial = null;
        }

        // get block at position
        Block skullBlock = w.getBlockAt(x, y, z);

        // set the block to air before setting it to a head, to be sure
        skullBlock.setType(Material.AIR);

        skullBlock.setType(skullMaterial);

        
        //NBTTileEntity nbtTileEntity = new NBTTileEntity(skullBlock.getState());
        
        
        

        try {
            //get package names
            Package obcPackage = Bukkit.getServer().getClass().getPackage();
            String obcPackageName = obcPackage.getName();
            String obcVersion = obcPackageName.substring(obcPackageName.lastIndexOf(".") + 1);
            String nmsPackageName = "net.minecraft.server." + obcVersion;

            //reflection setup            
            Class<?> nmsBlockPositionClass = Class.forName(nmsPackageName + ".BlockPosition");
            Constructor nmsBlockPositionConstructor = nmsBlockPositionClass.getDeclaredConstructor(int.class, int.class, int.class);

            Class<?> nmsItemStackClass = Class.forName(nmsPackageName + ".ItemStack");
            Method itemStackHasTag = nmsItemStackClass.getDeclaredMethod("hasTag");
            Method itemStackGetTag = nmsItemStackClass.getDeclaredMethod("getTag");

            Class<?> obcCraftItemStackClass = Class.forName(obcPackageName + ".inventory.CraftItemStack");
            Method craftItemStackAsNMSCopy = obcCraftItemStackClass.getDeclaredMethod("asNMSCopy", ItemStack.class);

            Class<?> nmsIBlockDataClass = Class.forName(nmsPackageName + ".IBlockData");

            Class<?> nmsWorldClass = Class.forName(nmsPackageName + ".World");
            Method worldSetTypeAndData = nmsWorldClass.getDeclaredMethod("setTypeAndData", nmsBlockPositionClass, nmsIBlockDataClass, int.class);
            Method worldGetTileEntity = nmsWorldClass.getDeclaredMethod("getTileEntity", nmsBlockPositionClass);

            Class<?> obcCraftWorldClass = Class.forName(obcPackageName + ".CraftWorld");
            Method craftWorldGetHandle = obcCraftWorldClass.getDeclaredMethod("getHandle");

            Class<?> nmsEnumDirectionClass = Class.forName(nmsPackageName + ".EnumDirection");
            Field downField = nmsEnumDirectionClass.getDeclaredField("DOWN");

            Class<?> nmsBlocksClass = Class.forName(nmsPackageName + ".Blocks");
            Field skullField;
            if (version.getMinor() < 13) {
                skullField = nmsBlocksClass.getDeclaredField("SKULL");
            } else {
                if(isWallHead(attachmentDirection)){
                    skullField = nmsBlocksClass.getDeclaredField("PLAYER_WALL_HEAD");
                } else {
                    skullField = nmsBlocksClass.getDeclaredField("PLAYER_HEAD");
                }
            }

            Class<?> nmsTileEntitySkullClass = Class.forName(nmsPackageName + ".TileEntitySkull");
            Method tileEntitySkullSetGameProfile = nmsTileEntitySkullClass.getDeclaredMethod("setGameProfile", GameProfile.class);

            Class<?> nmsBlockSkullClass = Class.forName(nmsPackageName + ".BlockSkull");
            Method blockSkullGetBlockData;
            if (version.getMinor() > 12) {
                blockSkullGetBlockData = nmsBlockSkullClass.getSuperclass().getSuperclass().getSuperclass().getDeclaredMethod("getBlockData");
            } else {
                blockSkullGetBlockData = nmsBlockSkullClass.getSuperclass().getSuperclass().getDeclaredMethod("getBlockData");
            }

            Class<?> nmsNbtTagCompoundClass = Class.forName(nmsPackageName + ".NBTTagCompound");
            Method nbtTagCompoundHasKeyOfType = nmsNbtTagCompoundClass.getDeclaredMethod("hasKeyOfType", String.class, int.class);
            Method nbtTagCompoundGetCompound = nmsNbtTagCompoundClass.getDeclaredMethod("getCompound", String.class);
            Method nbtTagCompoundGetString = nmsNbtTagCompoundClass.getDeclaredMethod("getString", String.class);

            Class<?> nmsGameProfileSerializerClass = Class.forName(nmsPackageName + ".GameProfileSerializer");
            Method gameProfileSerializerDeserialize = nmsGameProfileSerializerClass.getDeclaredMethod("deserialize", nmsNbtTagCompoundClass);

            //method itself
            //TODO set skull name
            //get nms ItemStack, World and BlockPosition
            Object bp = nmsBlockPositionConstructor.newInstance(x, y, z);
            Object nmsStack = craftItemStackAsNMSCopy.invoke(null, item);
            Object world = craftWorldGetHandle.invoke(w);

            //verify that the skull is not facing downturn;
            if (attachmentDirection == downField.get(null)) {
                log.log(Level.WARNING, "Block not placed: direction is downwards.");
                return;
            }

            //set block at that location to a skull
            Object blockData = blockSkullGetBlockData.invoke(skullField.get(null));
            int data;
            if(version.getMinor() < 13) {
                data = 3;
            } else {
                data = 0;
            }
            worldSetTypeAndData.invoke(world, bp, blockData, data);
            worldSetTypeAndData.invoke(world, bp, blockData, data);

            //check if the block has become a skull
            Object tileentity = worldGetTileEntity.invoke(world, bp);
            if (!nmsTileEntitySkullClass.isInstance(tileentity)) {
                log.severe("This should never happen");
                return;
            }
            Object tileentityskull = tileentity;

            // set gameprofile of item
            GameProfile gameprofile = null;

            if ((Boolean) itemStackHasTag.invoke(nmsStack)) {
                Object nbttagcompound = itemStackGetTag.invoke(nmsStack);
                if ((Boolean) nbtTagCompoundHasKeyOfType.invoke(nbttagcompound, "SkullOwner", 10)) {
                    gameprofile = (GameProfile) gameProfileSerializerDeserialize.invoke(null, nbtTagCompoundGetCompound.invoke(nbttagcompound, "SkullOwner"));
                } else if ((Boolean) nbtTagCompoundHasKeyOfType.invoke(nbttagcompound, "SkullOwner", 8) && ((String) nbtTagCompoundGetString.invoke(nbttagcompound, "SkullOwner")).length() > 0) {
                    gameprofile = new GameProfile((UUID) null, (String) nbtTagCompoundGetString.invoke(nbttagcompound, "SkullOwner"));
                } else {
                    log.fine("Could not find skull owner");
                }
            } else {
                log.fine("Could not find skull owner");
            }
            tileEntitySkullSetGameProfile.invoke(tileentityskull, gameprofile);

        } catch (ClassNotFoundException
                | NoSuchMethodException
                | SecurityException
                | NoSuchFieldException
                | IllegalArgumentException
                | IllegalAccessException
                | InvocationTargetException
                | InstantiationException ex) {
            log.log(Level.SEVERE, null, ex);
        }

        //Set facing or rotation (depending on attachment direction)
        if (version.getMinor() < 13) {
            BlockState skullState = skullBlock.getState();
            
            ((org.bukkit.material.Directional) skullState.getData()).setFacingDirection(attachmentDirection);
            if (!isWallHead(attachmentDirection)) {
                ((Skull) skullState).setRotation(rotation);
            }
            skullState.update(true);
        } else {
            try {
                Class<?> blockClass = Block.class;
                Class<?> blockDataClass = Class.forName("org.bukkit.block.data.BlockData");
                Method blockGetBlockDataMethod = blockClass.getDeclaredMethod("getBlockData");
                Method blockSetBlockDataMethod = blockClass.getDeclaredMethod("setBlockData", blockDataClass);
                
                Class<?> directionalClass = Class.forName("org.bukkit.block.data.Directional");
                Method directionalSetFacingMethod = directionalClass.getDeclaredMethod("setFacing", BlockFace.class);
                
                Class<?> rotatableClass = Class.forName("org.bukkit.block.data.Rotatable");
                Method rotatableSetRotationMethod = rotatableClass.getDeclaredMethod("setRotation", BlockFace.class);
                
//                Directional skullData = (Directional) skullBlock.getBlockData();
                Object skullData = blockGetBlockDataMethod.invoke(skullBlock);
                if (isWallHead(attachmentDirection)) {
//                    skullData.setFacing(attachmentDirection);
                    directionalSetFacingMethod.invoke(skullData, attachmentDirection);
                } else {
//                    skullData.setRotation(rotation);
                    rotatableSetRotationMethod.invoke(skullData, rotation);
                }
//                skullBlock.setBlockData(skullData);
                blockSetBlockDataMethod.invoke(skullBlock, skullData);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
    }

    private static boolean isWallHead(BlockFace attachmentDirection) {
        switch (attachmentDirection) {
            case UP:
            case DOWN:
            case SELF:
                return false;
            default:
                return true;
        }
    }

}
