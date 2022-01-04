package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.HeadUtils;
import com.github.cc007.headsplugin.business.services.NbtService;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTCompoundList;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTListCompound;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
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
    @Spy
    private HeadToItemstackMapperImpl headToItemstackMapper;

    @Test
    void getItemStacksHeads() {
        // prepare
        val name1 = "TestHead1";
        val name2 = "TestHead2";
        val uuid1 = UUID.randomUUID();
        val uuid2 = UUID.randomUUID();
        val value1 = "TestValue1";
        val value2 = "TestValue2";

        val expected1 = Mockito.mock(ItemStack.class);
        val expected2 = Mockito.mock(ItemStack.class);

        val head1 = Head.builder()
                .name(name1)
                .headOwner(uuid1)
                .value(value1)
                .build();
        val head2 = Head.builder()
                .name(name2)
                .headOwner(uuid2)
                .value(value2)
                .build();

        doReturn(expected1)
                .when(headToItemstackMapper).getItemStack(head1, 1);
        doReturn(expected2)
                .when(headToItemstackMapper).getItemStack(head2, 1);

        // execute
        val actual = headToItemstackMapper.getItemStacks(Arrays.asList(head1, head2));

        // verify
        verify(headToItemstackMapper).getItemStack(head1, 1);
        verify(headToItemstackMapper).getItemStack(head2, 1);

        assertThat(actual, containsInAnyOrder(expected1, expected2));
    }

    @Test
    void getItemStacksHeadsNull() {
        // prepare

        // execute
        val actualException = Assertions.assertThrows(NullPointerException.class, () ->
                headToItemstackMapper.getItemStacks(null)
        );

        // verify
        assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
    }

    @Test
    void getItemStacksHeadsQuantity() {
        // prepare
        val name1 = "TestHead1";
        val name2 = "TestHead2";
        val uuid1 = UUID.randomUUID();
        val uuid2 = UUID.randomUUID();
        val value1 = "TestValue1";
        val value2 = "TestValue2";
        val quantity = 42;

        val expected1 = Mockito.mock(ItemStack.class);
        val expected2 = Mockito.mock(ItemStack.class);

        val head1 = Head.builder()
                .name(name1)
                .headOwner(uuid1)
                .value(value1)
                .build();
        val head2 = Head.builder()
                .name(name2)
                .headOwner(uuid2)
                .value(value2)
                .build();

        doReturn(expected1)
                .when(headToItemstackMapper).getItemStack(head1, 42);
        doReturn(expected2)
                .when(headToItemstackMapper).getItemStack(head2, 42);

        // execute
        val actual = headToItemstackMapper.getItemStacks(Arrays.asList(head1, head2), 42);

        // verify
        verify(headToItemstackMapper).getItemStack(head1, 42);
        verify(headToItemstackMapper).getItemStack(head2, 42);

        assertThat(actual, containsInAnyOrder(expected1, expected2));
    }

    @Test
    void getItemStacksHeadsNullQuantity() {
        // prepare

        // execute
        val actualException = Assertions.assertThrows(NullPointerException.class, () ->
                headToItemstackMapper.getItemStacks(null, 42)
        );

        // verify
        assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
    }

    @Test
    void getItemStackHead() {
        // prepare
        val name = "TestHead";
        val uuid = UUID.randomUUID();
        val value = "TestValue";

        val expected = Mockito.mock(ItemStack.class);

        val head = Head.builder()
                .name(name)
                .headOwner(uuid)
                .value(value)
                .build();

        doReturn(expected)
                .when(headToItemstackMapper).getItemStack(head, 1);

        // execute
        val actual = headToItemstackMapper.getItemStack(head);

        // verify
        verify(headToItemstackMapper).getItemStack(head, 1);

        assertThat(actual, sameInstance(expected));
    }

    @Test
    void getItemStackHeadNull() {
        // prepare

        // execute
        val actualException = Assertions.assertThrows(NullPointerException.class, () ->
                headToItemstackMapper.getItemStack(null)
        );

        // verify
        assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
    }

    @Test
    void getItemStackHeadQuantity() {
        // prepare
        try (
                MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class);
        ) {
            val encoder = Base64.getEncoder();
            val notchUuid = UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5");
            val name = "TestHead";
            val uuid = UUID.randomUUID();
            val uuidIntArray = new int[]{1, 2, 3, 4};

            // Base64 encoded version of {"textures":{"SKIN":{"url":"http://textures.minecraft.net/texture/74e9c6e98582ffd8ff8feb3322cd1849c43fb16b158abb11ca7b42eda7743eb"}}}
            val value = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRlOWM2ZTk4NTgyZmZkOGZmOGZlYjMzMjJjZDE4NDljNDNmYjE2YjE1OGFiYjExY2E3YjQyZWRhNzc0M2ViIn19fQ";
            val quantity = 42;
            val head = Head.builder()
                    .name(name)
                    .headOwner(uuid)
                    .value(value)
                    .build();

            val itemFactory = Mockito.mock(ItemFactory.class);
            val skullMeta = Mockito.mock(SkullMeta.class);
            val offlinePlayer = Mockito.mock(OfflinePlayer.class);

            val nbtItem = Mockito.mock(NBTItem.class);
            val displayCompound = Mockito.mock(NBTCompound.class);
            val skullOwnerCompound = Mockito.mock(NBTCompound.class);
            val propertiesCompound = Mockito.mock(NBTCompound.class);
            val texturesCompoundList = Mockito.mock(NBTCompoundList.class);
            val textureListCompound = Mockito.mock(NBTListCompound.class);
            val headItemStack = Mockito.mock(ItemStack.class);

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
            val actual = headToItemstackMapper.getItemStack(head, quantity);

            // verify
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

    @Test
    void getItemStackHeadNullQuantity() {
        // prepare

        // execute
        val actualException = Assertions.assertThrows(NullPointerException.class, () ->
                headToItemstackMapper.getItemStack(null, 42)
        );

        // verify
        assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
    }
}