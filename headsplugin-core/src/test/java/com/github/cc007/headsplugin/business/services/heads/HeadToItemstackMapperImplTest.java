package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.utils.HeadUtils;
import com.github.cc007.headsplugin.business.services.OwnerProfileService;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
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
    private OwnerProfileService ownerProfileService;

    @InjectMocks
    @Spy
    private HeadToItemstackMapperImpl headToItemstackMapper;

    @Test
    void getItemStacksHeads() {
        // prepare
        final var name1 = "TestHead1";
        final var name2 = "TestHead2";
        final var uuid1 = UUID.randomUUID();
        final var uuid2 = UUID.randomUUID();
        final var value1 = "TestValue1";
        final var value2 = "TestValue2";

        final var expected1 = Mockito.mock(ItemStack.class);
        final var expected2 = Mockito.mock(ItemStack.class);

        final var head1 = Head.builder()
                .name(name1)
                .headOwner(uuid1)
                .value(value1)
                .build();
        final var head2 = Head.builder()
                .name(name2)
                .headOwner(uuid2)
                .value(value2)
                .build();

        doReturn(expected1)
                .when(headToItemstackMapper).getItemStack(head1, 1);
        doReturn(expected2)
                .when(headToItemstackMapper).getItemStack(head2, 1);

        // execute
        final var actual = headToItemstackMapper.getItemStacks(List.of(head1, head2));

        // verify
        verify(headToItemstackMapper).getItemStack(head1, 1);
        verify(headToItemstackMapper).getItemStack(head2, 1);

        assertThat(actual, containsInAnyOrder(expected1, expected2));
    }

    @Test
    void getItemStacksHeadsNull() {
        // prepare

        // execute
        final var actualException = Assertions.assertThrows(NullPointerException.class, () ->
                headToItemstackMapper.getItemStacks(null)
        );

        // verify
        assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
    }

    @Test
    void getItemStacksHeadsQuantity() {
        // prepare
        final var name1 = "TestHead1";
        final var name2 = "TestHead2";
        final var uuid1 = UUID.randomUUID();
        final var uuid2 = UUID.randomUUID();
        final var value1 = "TestValue1";
        final var value2 = "TestValue2";
        final var quantity = 42;

        final var expected1 = Mockito.mock(ItemStack.class);
        final var expected2 = Mockito.mock(ItemStack.class);

        final var head1 = Head.builder()
                .name(name1)
                .headOwner(uuid1)
                .value(value1)
                .build();
        final var head2 = Head.builder()
                .name(name2)
                .headOwner(uuid2)
                .value(value2)
                .build();

        doReturn(expected1)
                .when(headToItemstackMapper).getItemStack(head1, 42);
        doReturn(expected2)
                .when(headToItemstackMapper).getItemStack(head2, 42);

        // execute
        final var actual = headToItemstackMapper.getItemStacks(List.of(head1, head2), 42);

        // verify
        verify(headToItemstackMapper).getItemStack(head1, 42);
        verify(headToItemstackMapper).getItemStack(head2, 42);

        assertThat(actual, containsInAnyOrder(expected1, expected2));
    }

    @Test
    void getItemStacksHeadsNullQuantity() {
        // prepare

        // execute
        final var actualException = Assertions.assertThrows(NullPointerException.class, () ->
                headToItemstackMapper.getItemStacks(null, 42)
        );

        // verify
        assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
    }

    @Test
    void getItemStackHead() {
        // prepare
        final var name = "TestHead";
        final var uuid = UUID.randomUUID();
        final var value = "TestValue";

        final var expected = Mockito.mock(ItemStack.class);

        final var head = Head.builder()
                .name(name)
                .headOwner(uuid)
                .value(value)
                .build();

        doReturn(expected)
                .when(headToItemstackMapper).getItemStack(head, 1);

        // execute
        final var actual = headToItemstackMapper.getItemStack(head);

        // verify
        verify(headToItemstackMapper).getItemStack(head, 1);

        assertThat(actual, sameInstance(expected));
    }

    @Test
    void getItemStackHeadNull() {
        // prepare

        // execute
        final var actualException = Assertions.assertThrows(NullPointerException.class, () ->
                headToItemstackMapper.getItemStack(null)
        );

        // verify
        assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
    }

    @Test
    void getItemStackHeadQuantity() throws Exception {
        // prepare
        try (
                MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class);
        ) {
            final var encoder = Base64.getEncoder();
            final var notchUuid = UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5");
            final var name = "TestHead";
            final var uuid = UUID.randomUUID();
            final var uuidIntArray = new int[]{1, 2, 3, 4};

            // Base64 encoded version of {"textures":{"SKIN":{"url":"http://textures.minecraft.net/texture/74e9c6e98582ffd8ff8feb3322cd1849c43fb16b158abb11ca7b42eda7743eb"}}}
            final var value = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRlOWM2ZTk4NTgyZmZkOGZmOGZlYjMzMjJjZDE4NDljNDNmYjE2YjE1OGFiYjExY2E3YjQyZWRhNzc0M2ViIn19fQ";
            final var skinUrl = "http://textures.minecraft.net/texture/74e9c6e98582ffd8ff8feb3322cd1849c43fb16b158abb11ca7b42eda7743eb";
            final var quantity = 42;
            final var head = Head.builder()
                    .name(name)
                    .headOwner(uuid)
                    .value(value)
                    .build();

            final var itemFactory = Mockito.mock(ItemFactory.class);
            final var skullMeta = Mockito.mock(SkullMeta.class);
            final var ownerProfile = Mockito.mock(PlayerProfile.class);
            final var textures = Mockito.mock(PlayerTextures.class);
            final var offlinePlayer = Mockito.mock(OfflinePlayer.class);

            final var urlCaptor = ArgumentCaptor.forClass(URL.class);

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
            bukkit.when(() -> Bukkit.createPlayerProfile(uuid, name))
                    .thenReturn(ownerProfile);
            when(ownerProfileService.createOwnerProfile(head))
                    .thenReturn(ownerProfile);
//            when(ownerProfile.getTextures())
//                    .thenReturn(textures);


            // execute
            final var actual = headToItemstackMapper.getItemStack(head, quantity);

            // verify
            bukkit.verify(Bukkit::getItemFactory, times(4));
            bukkit.verify(() -> Bukkit.getOfflinePlayer(notchUuid));
            //bukkit.verify(() -> Bukkit.createPlayerProfile(uuid, name));
            bukkit.verifyNoMoreInteractions();

            verify(skullMeta).setOwningPlayer(offlinePlayer);
            verify(skullMeta).setDisplayName(head.getName());
            //verify(textures).setSkin(urlCaptor.capture());
            verify(skullMeta).setOwnerProfile(ownerProfile);
            verifyNoMoreInteractions(headUtils, ownerProfileService, itemFactory/*, skullMeta*/);

            assertThat(actual.getType(), is(Material.PLAYER_HEAD));
            assertThat(actual.getAmount(), is(quantity));
            assertThat(actual.getItemMeta(), isA(SkullMeta.class));
            SkullMeta actualSkullMeta = (SkullMeta) actual.getItemMeta();
            assertThat(actualSkullMeta, notNullValue());
            //assertThat(urlCaptor.getValue().toString(), is(skinUrl));
        }
    }

    @Test
    void getItemStackHeadNullQuantity() {
        // prepare

        // execute
        final var actualException = Assertions.assertThrows(NullPointerException.class, () ->
                headToItemstackMapper.getItemStack(null, 42)
        );

        // verify
        assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
    }
}