package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.utils.HeadUtils;
import com.github.cc007.headsplugin.business.services.OwnerProfileService;
import com.github.cc007.headsplugin.business.services.heads.utils.HeadUtilsImpl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class HeadPlacerImplTest {

    @Spy
    HeadUtils headUtils = new HeadUtilsImpl();

    @Mock
    OwnerProfileService ownerProfileService;

    @InjectMocks
    HeadPlacerImpl headPlacer;

    @SuppressWarnings("ConstantConditions")
    @Nested
    class NullParameterValidation {
        @Test
        void placeHeadILBHeadItemStackNull() {
            // prepare

            // execute
            final var actualException = Assertions.assertThrows(NullPointerException.class, () ->
                    headPlacer.placeHead((ItemStack) null, new Location(null, 0, 0, 0), BlockFace.NORTH)
            );

            // verify
            assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        }

        @Test
        void placeHeadILBLocationNull() {
            // prepare

            // execute
            final var actualException = Assertions.assertThrows(NullPointerException.class, () ->
                    headPlacer.placeHead(new ItemStack(Material.PLAYER_HEAD), null, BlockFace.NORTH)
            );

            // verify
            assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        }

        @Test
        void placeHeadILBRotationNull() {
            // prepare

            // execute
            final var actualException = Assertions.assertThrows(NullPointerException.class, () ->
                    headPlacer.placeHead(new ItemStack(Material.PLAYER_HEAD), new Location(null, 0, 0, 0), null)
            );

            // verify
            assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        }

        @Test
        void placeHeadHLBHeadNull() {
            // prepare

            // execute
            final var actualException = Assertions.assertThrows(NullPointerException.class, () ->
                    headPlacer.placeHead((Head) null, new Location(null, 0, 0, 0), BlockFace.NORTH)
            );

            // verify
            assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        }

        @Test
        void placeHeadHLBLocationNull() {
            // prepare

            // execute
            final var actualException = Assertions.assertThrows(NullPointerException.class, () ->
                    headPlacer.placeHead(Head.builder().build(), null, BlockFace.NORTH)
            );

            // verify
            assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        }

        @Test
        void placeHeadHLBRotationNull() {
            // prepare

            // execute
            final var actualException = Assertions.assertThrows(NullPointerException.class, () ->
                    headPlacer.placeHead(Head.builder().build(), new Location(null, 0, 0, 0), null)
            );

            // verify
            assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        }

        @Test
        void placeWallHeadILBHeadItemStackNull() {
            // prepare

            // execute
            final var actualException = Assertions.assertThrows(NullPointerException.class, () ->
                    headPlacer.placeWallHead((ItemStack) null, new Location(null, 0, 0, 0), BlockFace.NORTH)
            );

            // verify
            assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        }

        @Test
        void placeWallHeadILBLocationNull() {
            // prepare

            // execute
            final var actualException = Assertions.assertThrows(NullPointerException.class, () ->
                    headPlacer.placeWallHead(new ItemStack(Material.PLAYER_HEAD), null, BlockFace.NORTH)
            );

            // verify
            assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        }

        @Test
        void placeWallHeadILBRotationNull() {
            // prepare

            // execute
            final var actualException = Assertions.assertThrows(NullPointerException.class, () ->
                    headPlacer.placeWallHead(new ItemStack(Material.PLAYER_HEAD), new Location(null, 0, 0, 0), null)
            );

            // verify
            assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        }

        @Test
        void placeWallHeadHLBHeadNull() {
            // prepare

            // execute
            final var actualException = Assertions.assertThrows(NullPointerException.class, () ->
                    headPlacer.placeWallHead((Head) null, new Location(null, 0, 0, 0), BlockFace.NORTH)
            );

            // verify
            assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        }

        @Test
        void placeWallHeadHLBLocationNull() {
            // prepare

            // execute
            final var actualException = Assertions.assertThrows(NullPointerException.class, () ->
                    headPlacer.placeWallHead(Head.builder().build(), null, BlockFace.NORTH)
            );

            // verify
            assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        }

        @Test
        void placeWallHeadHLBRotationNull() {
            // prepare

            // execute
            final var actualException = Assertions.assertThrows(NullPointerException.class, () ->
                    headPlacer.placeWallHead(Head.builder().build(), new Location(null, 0, 0, 0), null)
            );

            // verify
            assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        }
    }

    @Nested
    class OtherParameterValidation {
        @Test
        void placeHeadILBHeadItemStackNotPlayerHead() {
            // prepare

            // execute
            final var actualException = Assertions.assertThrows(IllegalArgumentException.class, () ->
                    headPlacer.placeHead(new ItemStack(Material.DIRT), new Location(null, 0, 0, 0), BlockFace.NORTH)
            );

            // verify
            assertThat(actualException.getMessage(), is("The Material of the provided ItemStack is not equal to PLAYER_HEAD"));
        }

        @Test
        void placeWallHeadILBHeadItemStackNotPlayerHead() {
            // prepare

            // execute
            final var actualException = Assertions.assertThrows(IllegalArgumentException.class, () ->
                    headPlacer.placeWallHead(new ItemStack(Material.DIRT), new Location(null, 0, 0, 0), BlockFace.NORTH)
            );

            // verify
            assertThat(actualException.getMessage(), is("The Material of the provided ItemStack is not equal to PLAYER_HEAD"));
        }
    }

    @Test
    void placeHeadILB() {
        // prepare
        final var testHeadItemStack = mock(ItemStack.class);
        final var testWorld = mock(World.class);
        final var testLocation = new Location(testWorld, 0, 0, 0);
        final var testRotation = BlockFace.NORTH;
        final var testBlock = mock(Block.class);
        final var testRotatable = mock(Rotatable.class);
        final var testSkullMeta = mock(SkullMeta.class);
        final var testPlayerProfile = mock(PlayerProfile.class);
        final var testSkull = mock(Skull.class);

        doReturn(Material.PLAYER_HEAD)
                .when(testHeadItemStack).getType();
        doReturn(testBlock)
                .when(testWorld).getBlockAt(testLocation);

        doReturn(testRotatable)
                .when(testBlock).getBlockData();

        doReturn(testSkullMeta)
                .when(testHeadItemStack).getItemMeta();
        doReturn(testPlayerProfile)
                .when(testSkullMeta).getOwnerProfile();

        doReturn(testSkull)
                .when(testBlock).getState();

        // execute
        headPlacer.placeHead(testHeadItemStack, testLocation, testRotation);

        // verify
        verify(testBlock).setType(eq(Material.PLAYER_HEAD));

        verify(testRotatable).setRotation(testRotation);
        verify(testBlock).setBlockData(testRotatable);

        verify(testSkull).setOwnerProfile(testPlayerProfile);
        verify(testSkull).update();

        verifyNoMoreInteractions(ownerProfileService, headUtils);
        verifyNoMoreInteractions(testHeadItemStack, testWorld, testBlock, testRotatable);
    }

    @Test
    void placeHeadHLB() {
        // prepare
        final var testHead = Head.builder()
                .headOwner(UUID.fromString("01234567-89ab-cdef-fedc-ba9876543210"))
                .name("TestName")
                .value("TestValue")
                .build();
        final var testWorld = mock(World.class);
        final var testLocation = new Location(testWorld, 0, 0, 0);
        final var testRotation = BlockFace.NORTH;
        final var testBlock = mock(Block.class);
        final var testRotatable = mock(Rotatable.class);
        final var testPlayerProfile = mock(PlayerProfile.class);
        final var testSkull = mock(Skull.class);

        doReturn(testBlock)
                .when(testWorld).getBlockAt(testLocation);

        doReturn(testRotatable)
                .when(testBlock).getBlockData();

        doReturn(testPlayerProfile)
                .when(ownerProfileService).createOwnerProfile(testHead);

        doReturn(testSkull)
                .when(testBlock).getState();


        // execute
        headPlacer.placeHead(testHead, testLocation, testRotation);

        // verify
        verify(testBlock).setType(eq(Material.PLAYER_HEAD));

        verify(testRotatable).setRotation(testRotation);
        verify(testBlock).setBlockData(testRotatable);

        verify(testSkull).setOwnerProfile(testPlayerProfile);
        verify(testSkull).update();

        verifyNoMoreInteractions(ownerProfileService, headUtils);
        verifyNoMoreInteractions(testWorld, testBlock, testRotatable);
    }

    @Test
    void placeWallHeadILB() {
        // prepare
        final var testHeadItemStack = mock(ItemStack.class);
        final var testWorld = mock(World.class);
        final var testLocation = new Location(testWorld, 0, 0, 0);
        final var testDirection = BlockFace.NORTH;
        final var testBlock = mock(Block.class);
        final var testDirectional = mock(Directional.class);
        final var testSkullMeta = mock(SkullMeta.class);
        final var testPlayerProfile = mock(PlayerProfile.class);
        final var testSkull = mock(Skull.class);

        doReturn(Material.PLAYER_HEAD)
                .when(testHeadItemStack).getType();
        doReturn(testBlock)
                .when(testWorld).getBlockAt(testLocation);

        doReturn(testDirectional)
                .when(testBlock).getBlockData();

        doReturn(testSkullMeta)
                .when(testHeadItemStack).getItemMeta();
        doReturn(testPlayerProfile)
                .when(testSkullMeta).getOwnerProfile();

        doReturn(testSkull)
                .when(testBlock).getState();



        // execute
        headPlacer.placeWallHead(testHeadItemStack, testLocation, testDirection);

        // verify
        verify(testBlock).setType(eq(Material.PLAYER_WALL_HEAD));

        verify(testDirectional).setFacing(testDirection);
        verify(testBlock).setBlockData(testDirectional);

        verify(testSkull).setOwnerProfile(testPlayerProfile);
        verify(testSkull).update();

        verifyNoMoreInteractions(ownerProfileService, headUtils);
        verifyNoMoreInteractions(testHeadItemStack, testWorld, testBlock, testDirectional);
    }

    @Test
    void placeWallHeadHLB() {
        // prepare
        final var testHead = Head.builder()
                .headOwner(UUID.fromString("01234567-89ab-cdef-fedc-ba9876543210"))
                .name("TestName")
                .value("TestValue")
                .build();
        final var testWorld = mock(World.class);
        final var testLocation = new Location(testWorld, 0, 0, 0);
        final var testDirection = BlockFace.NORTH;
        final var testBlock = mock(Block.class);
        final var testDirectional = mock(Directional.class);
        final var testPlayerProfile = mock(PlayerProfile.class);
        final var testSkull = mock(Skull.class);

        doReturn(testBlock)
                .when(testWorld).getBlockAt(testLocation);

        doReturn(testDirectional)
                .when(testBlock).getBlockData();

        doReturn(testPlayerProfile)
                .when(ownerProfileService).createOwnerProfile(testHead);

        doReturn(testSkull)
                .when(testBlock).getState();

        // execute
        headPlacer.placeWallHead(testHead, testLocation, testDirection);

        // verify
        verify(testBlock).setType(eq(Material.PLAYER_WALL_HEAD));

        verify(testDirectional).setFacing(testDirection);
        verify(testBlock).setBlockData(testDirectional);

        verify(testSkull).setOwnerProfile(testPlayerProfile);
        verify(testSkull).update();

        verifyNoMoreInteractions(ownerProfileService, headUtils);
        verifyNoMoreInteractions(testWorld, testBlock, testDirectional);
    }
}