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
import com.github.cc007.headsplugin.legacy.utils.heads.Head;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class HeadCreator {

    /**
     * Get a bukkit <code>ItemStack</code> based on the provided
     * <code>Head</code>
     *
     * @param head the provided <code>Head</code>
     * @return The <code>ItemStack</code> based on the provided
     * <code>Head</code>
     */
    public static ItemStack getItemStack(Head head) {
        return getItemStack(head, 1);
    }

    /**
     * Get a List of bukkit <code>ItemStack</code> objects based on the provided
     * List of <code>Head</code> objects
     *
     * @param heads the provided List of <code>Head</code> objects
     * @return The List of bukkit <code>ItemStack</code> objects based on the
     * provided List of <code>Head</code> objects
     */
    public static List<ItemStack> getItemStacks(List<Head> heads) {
        return getItemStacks(heads, 1);
    }

    /**
     * Get a bukkit <code>ItemStack</code> based on the provided
     * <code>Head</code>
     *
     * @param head the provided <code>Head</code>
     * @param quantity the number of heads in the <code>ItemStack</code>
     * @return The <code>ItemStack</code> based on the provided
     * <code>Head</code>
     */
    public static ItemStack getItemStack(Head head, int quantity) {
        //get version
        MinecraftVersion version = new MinecraftVersion();
        
        //get package names
        Package obcPackage = Bukkit.getServer().getClass().getPackage();
        String obcPackageName = obcPackage.getName();
        String obcVersion = obcPackageName.substring(obcPackageName.lastIndexOf(".") + 1);
        String nmsPackageName = "net.minecraft.server." + obcVersion;
        
        //get head name
        String skullName;
        if(version.getMinor() > 12){
            skullName = "PLAYER_HEAD";
        } else {
            skullName = "SKULL";
        }
        try {
            if (head == null) {
                Class<?> obMaterialClass = Class.forName("org.bukkit.Material");
                Field skull = obMaterialClass.getDeclaredField(skullName);

                return new ItemStack((Material)skull.get(null), quantity, (short)0, (byte)4);
            }
            
            //reflection setup
            Class<?> nmsNbtBaseClass = Class.forName(nmsPackageName + ".NBTBase");

            Class<?> nmsNbtTagCompoundClass = Class.forName(nmsPackageName + ".NBTTagCompound");
            Method nbtSetString = nmsNbtTagCompoundClass.getDeclaredMethod("setString", String.class, String.class);
            Method nbtSet = nmsNbtTagCompoundClass.getDeclaredMethod("set", String.class, nmsNbtBaseClass);

            Class<?> nmsNbtTagListClass = Class.forName(nmsPackageName + ".NBTTagList");
            Method add = nmsNbtTagListClass.getDeclaredMethod("add", nmsNbtBaseClass);

            Class<?> nmsItemClass = Class.forName(nmsPackageName + ".Item");

            Class<?> nmsItemStackClass = Class.forName(nmsPackageName + ".ItemStack");
            Method itemStackSetTag = nmsItemStackClass.getDeclaredMethod("setTag", nmsNbtTagCompoundClass);
            Method itemStackGetTag = nmsItemStackClass.getDeclaredMethod("getTag");

            Class<?> nmsItemsClass = Class.forName(nmsPackageName + ".Items");
            Field skullField = nmsItemsClass.getDeclaredField(skullName);

            Class<?> obcCraftItemStackClass = Class.forName(obcPackageName + ".inventory.CraftItemStack");
            Method craftItemStackasBukkitCopy = obcCraftItemStackClass.getDeclaredMethod("asBukkitCopy", nmsItemStackClass);

            //actual method
            Object displayTag = nmsNbtTagCompoundClass.newInstance();
            if(version.getMinor() > 12){
                nbtSetString.invoke(displayTag, "Name", "\"" + head.getName() + "\"");
            } else {
                nbtSetString.invoke(displayTag, "Name", head.getName());
            }

            Object entryTag = nmsNbtTagCompoundClass.newInstance();
            nbtSetString.invoke(entryTag, "Value", head.getValue());

            Object texturesList = nmsNbtTagListClass.newInstance();
            add.invoke(texturesList, entryTag);

            Object propertiesTag = nmsNbtTagCompoundClass.newInstance();
            nbtSet.invoke(propertiesTag, "textures", texturesList);

            Object skullOwnerTag = nmsNbtTagCompoundClass.newInstance();
            nbtSetString.invoke(skullOwnerTag, "Id", head.getHeadOwner().toString());
            nbtSet.invoke(skullOwnerTag, "Properties", propertiesTag);

            Object skullItem = skullField.get(null);

            Object nmsStack;
            if(version.getMinor() > 12){
                Class<?> nmsIMaterialClass = Class.forName(nmsPackageName + ".IMaterial");
                Class<?> nmsBlockClass = Class.forName(nmsPackageName + ".Block");
                Method blockAsBlock = nmsBlockClass.getDeclaredMethod("asBlock", nmsItemClass);
                
                Object playerBlock = blockAsBlock.invoke(null, skullItem);
                Constructor nmsItemStackConstructorNim = nmsItemStackClass.getDeclaredConstructor(nmsIMaterialClass);
                nmsStack = nmsItemStackConstructorNim.newInstance(playerBlock);
            } else {
                Constructor nmsItemStackConstructorNicII = nmsItemStackClass.getDeclaredConstructor(nmsItemClass, int.class, int.class);
                nmsStack = nmsItemStackConstructorNicII.newInstance(skullItem, quantity, (byte) SkullType.PLAYER.ordinal());
            }
            
            itemStackSetTag.invoke(nmsStack, nmsNbtTagCompoundClass.newInstance());
            Object nmsStackTag = itemStackGetTag.invoke(nmsStack);
            nbtSet.invoke(nmsStackTag, "display", displayTag);
            nbtSet.invoke(nmsStackTag, "SkullOwner", skullOwnerTag);

            return (ItemStack) craftItemStackasBukkitCopy.invoke(null, nmsStack);

        } catch (ClassNotFoundException | SecurityException | InstantiationException | IllegalAccessException | NoSuchMethodException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException ex) {
            Bukkit.getLogger().log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Get a List of bukkit <code>ItemStack</code> objects based on the provided
     * List of <code>Head</code> objects
     *
     * @param heads the provided List of <code>Head</code> objects
     * @param quantity the number of heads in the <code>ItemStack</code> objects
     * @return The List of bukkit <code>ItemStack</code> objects based on the
     * provided List of <code>Head</code> objects
     */
    public static List<ItemStack> getItemStacks(List<Head> heads, int quantity) {
        List<ItemStack> stackList = new ArrayList<>();
        for (Head head : heads) {
            stackList.add(getItemStack(head, quantity));
        }
        return stackList;
    }
}
