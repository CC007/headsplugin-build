package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.services.heads.HeadUtils;
import com.github.cc007.headsplugin.business.services.NbtService;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTCompoundList;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTListCompound;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HeadToItemstackMapperImplTest {

    @Mock
    private HeadUtils headUtils;

    @Mock
    private NbtService nbtService;

    @InjectMocks
    private HeadToItemstackMapperImpl headToItemstackMapper;

    @Test
    void getItemStacksHeads() {
        // prepare

        // execute

        // verify
    }

    @Test
    void getItemStacksHeadsQuantity() {
        // prepare

        // execute

        // verify
    }

    @Test
    void getItemStackHead() {
        // prepare

        // execute

        // verify
    }

    @Test
    void getItemStackHeadQuantityBefore1_16() {
        // prepare
        try (
                MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class);
                MockedStatic<MinecraftVersion> minecraftVersion = Mockito.mockStatic(MinecraftVersion.class);
        ) {
            Base64.Encoder encoder = Base64.getEncoder();
            UUID notchUuid = UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5");
            String name = "TestHead";
            UUID uuid = UUID.randomUUID();
            // Base64 encoded version of {"textures":{"SKIN":{"url":"http://textures.minecraft.net/texture/74e9c6e98582ffd8ff8feb3322cd1849c43fb16b158abb11ca7b42eda7743eb"}}}
            String value = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRlOWM2ZTk4NTgyZmZkOGZmOGZlYjMzMjJjZDE4NDljNDNmYjE2YjE1OGFiYjExY2E3YjQyZWRhNzc0M2ViIn19fQ";
            int quantity = 42;
            Head head = Head.builder()
                    .name(name)
                    .headOwner(uuid)
                    .value(value)
                    .build();

            ItemFactory itemFactory = Mockito.mock(ItemFactory.class);
            SkullMeta skullMeta = Mockito.mock(SkullMeta.class);
            OfflinePlayer offlinePlayer = Mockito.mock(OfflinePlayer.class);

            NBTItem nbtItem = Mockito.mock(NBTItem.class);
            NBTCompound displayCompound = Mockito.mock(NBTCompound.class);
            NBTCompound skullOwnerCompound = Mockito.mock(NBTCompound.class);
            NBTCompound propertiesCompound = Mockito.mock(NBTCompound.class);
            NBTCompoundList texturesCompoundList = Mockito.mock(NBTCompoundList.class);
            NBTListCompound textureListCompound = Mockito.mock(NBTListCompound.class);
            ItemStack headItemStack = Mockito.mock(ItemStack.class);

            minecraftVersion.when(MinecraftVersion::getVersion)
                    .thenReturn(MinecraftVersion.MC1_15_R1);
            bukkit.when(Bukkit::getItemFactory)
                    .thenReturn(itemFactory);
            when(itemFactory.getItemMeta(Material.PLAYER_HEAD))
                    .thenReturn(skullMeta);
            bukkit.when(() -> Bukkit.getOfflinePlayer(notchUuid))
                    .thenReturn(offlinePlayer);
            when(itemFactory.isApplicable(skullMeta, Material.PLAYER_HEAD))
                    .thenReturn(true);
            when(itemFactory.asMetaFor(skullMeta, Material.PLAYER_HEAD))
                    .thenReturn(skullMeta);
            when(itemFactory.updateMaterial(skullMeta, Material.PLAYER_HEAD))
                    .thenReturn(Material.PLAYER_HEAD);
            when(skullMeta.clone())
                    .thenReturn(skullMeta);

            when(nbtService.getNbtItem(any(ItemStack.class)))
                    .thenReturn(nbtItem);
            when(nbtItem.addCompound("display"))
                    .thenReturn(displayCompound);
            when(nbtItem.addCompound("SkullOwner"))
                    .thenReturn(skullOwnerCompound);
            when(skullOwnerCompound.addCompound("Properties"))
                    .thenReturn(propertiesCompound);
            when(propertiesCompound.getCompoundList("textures"))
                    .thenReturn(texturesCompoundList);
            when(texturesCompoundList.addCompound())
                    .thenReturn(textureListCompound);
            when(nbtItem.getItem())
                    .thenReturn(headItemStack);


            // execute
            ItemStack actual = headToItemstackMapper.getItemStack(head, quantity);

            // verify
            minecraftVersion.verify(MinecraftVersion::getVersion);
            minecraftVersion.verifyNoMoreInteractions();
            bukkit.verify(Bukkit::getItemFactory, times(4));
            bukkit.verify(() -> Bukkit.getOfflinePlayer(notchUuid));
            bukkit.verifyNoMoreInteractions();

            verify(skullMeta).setOwningPlayer(offlinePlayer);
            verify(skullMeta).setDisplayName(head.getName());
            verify(displayCompound).setString("Name", "\"" + head.getName() + "\"");
            verify(skullOwnerCompound).setString("Id", head.getHeadOwner().toString());
            verify(skullOwnerCompound).setString("Name", head.getName());
            verify(textureListCompound).setString("Value", head.getValue());
            verifyNoMoreInteractions(headUtils, nbtService, itemFactory, skullMeta, nbtItem);

            assertThat(actual, is(headItemStack));
        }
    }

    @Test
    void getItemStackHeadQuantityAfter1_16() {
        // prepare
        try (
                MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class);
                MockedStatic<MinecraftVersion> minecraftVersion = Mockito.mockStatic(MinecraftVersion.class);
        ) {
            Base64.Encoder encoder = Base64.getEncoder();
            UUID notchUuid = UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5");
            String name = "TestHead";
            UUID uuid = UUID.randomUUID();
            int[] uuidIntArray = new int[]{1, 2, 3, 4};

            // Base64 encoded version of {"textures":{"SKIN":{"url":"http://textures.minecraft.net/texture/74e9c6e98582ffd8ff8feb3322cd1849c43fb16b158abb11ca7b42eda7743eb"}}}
            String value = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRlOWM2ZTk4NTgyZmZkOGZmOGZlYjMzMjJjZDE4NDljNDNmYjE2YjE1OGFiYjExY2E3YjQyZWRhNzc0M2ViIn19fQ";
            int quantity = 42;
            Head head = Head.builder()
                    .name(name)
                    .headOwner(uuid)
                    .value(value)
                    .build();

            ItemFactory itemFactory = Mockito.mock(ItemFactory.class);
            SkullMeta skullMeta = Mockito.mock(SkullMeta.class);
            OfflinePlayer offlinePlayer = Mockito.mock(OfflinePlayer.class);

            NBTItem nbtItem = Mockito.mock(NBTItem.class);
            NBTCompound displayCompound = Mockito.mock(NBTCompound.class);
            NBTCompound skullOwnerCompound = Mockito.mock(NBTCompound.class);
            NBTCompound propertiesCompound = Mockito.mock(NBTCompound.class);
            NBTCompoundList texturesCompoundList = Mockito.mock(NBTCompoundList.class);
            NBTListCompound textureListCompound = Mockito.mock(NBTListCompound.class);
            ItemStack headItemStack = Mockito.mock(ItemStack.class);

            minecraftVersion.when(MinecraftVersion::getVersion)
                    .thenReturn(MinecraftVersion.MC1_16_R1);
            bukkit.when(Bukkit::getItemFactory)
                    .thenReturn(itemFactory);
            when(itemFactory.getItemMeta(Material.PLAYER_HEAD))
                    .thenReturn(skullMeta);
            bukkit.when(() -> Bukkit.getOfflinePlayer(notchUuid))
                    .thenReturn(offlinePlayer);
            when(itemFactory.isApplicable(skullMeta, Material.PLAYER_HEAD))
                    .thenReturn(true);
            when(itemFactory.asMetaFor(skullMeta, Material.PLAYER_HEAD))
                    .thenReturn(skullMeta);
            when(itemFactory.updateMaterial(skullMeta, Material.PLAYER_HEAD))
                    .thenReturn(Material.PLAYER_HEAD);
            when(skullMeta.clone())
                    .thenReturn(skullMeta);

            when(nbtService.getNbtItem(any(ItemStack.class)))
                    .thenReturn(nbtItem);
            when(nbtItem.addCompound("display"))
                    .thenReturn(displayCompound);
            when(nbtItem.addCompound("SkullOwner"))
                    .thenReturn(skullOwnerCompound);
            when(headUtils.getIntArrayFromUuid(uuid))
                    .thenReturn(uuidIntArray);
            when(skullOwnerCompound.addCompound("Properties"))
                    .thenReturn(propertiesCompound);
            when(propertiesCompound.getCompoundList("textures"))
                    .thenReturn(texturesCompoundList);
            when(texturesCompoundList.addCompound())
                    .thenReturn(textureListCompound);
            when(nbtItem.getItem())
                    .thenReturn(headItemStack);


            // execute
            ItemStack actual = headToItemstackMapper.getItemStack(head, quantity);

            // verify
            minecraftVersion.verify(MinecraftVersion::getVersion);
            minecraftVersion.verifyNoMoreInteractions();
            bukkit.verify(Bukkit::getItemFactory, times(4));
            bukkit.verify(() -> Bukkit.getOfflinePlayer(notchUuid));
            bukkit.verifyNoMoreInteractions();

            verify(skullMeta).setOwningPlayer(offlinePlayer);
            verify(skullMeta).setDisplayName(head.getName());
            verify(displayCompound).setString("Name", "\"" + head.getName() + "\"");
            verify(skullOwnerCompound).setIntArray("Id", uuidIntArray);
            verify(skullOwnerCompound).setString("Name", head.getName());
            verify(textureListCompound).setString("Value", head.getValue());
            verifyNoMoreInteractions(headUtils, nbtService, itemFactory, skullMeta, nbtItem);

            assertThat(actual, is(headItemStack));
        }
    }
}