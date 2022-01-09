package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.HeadUtils;
import com.github.cc007.headsplugin.business.services.NbtService;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTTileEntity;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.inventory.ItemStack;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HeadPlacerImplTest {

    @Spy
    HeadUtils headUtils = new HeadUtilsImpl();

    @Mock
    NbtService nbtService;

    @InjectMocks
    HeadPlacerImpl headPlacer;

    @Nested
    class NullParameterValidation {
        @Test
        void placeHeadILBHeadItemStackNull() {
            // prepare

            // execute
            val actualException = Assertions.assertThrows(NullPointerException.class, () ->
                    headPlacer.placeHead((ItemStack) null, new Location(null, 0, 0, 0), BlockFace.NORTH)
            );

            // verify
            assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        }

        @Test
        void placeHeadILBLocationNull() {
            // prepare

            // execute
            val actualException = Assertions.assertThrows(NullPointerException.class, () ->
                    headPlacer.placeHead(new ItemStack(Material.PLAYER_HEAD), null, BlockFace.NORTH)
            );

            // verify
            assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        }

        @Test
        void placeHeadILBRotationNull() {
            // prepare

            // execute
            val actualException = Assertions.assertThrows(NullPointerException.class, () ->
                    headPlacer.placeHead(new ItemStack(Material.PLAYER_HEAD), new Location(null, 0, 0, 0), null)
            );

            // verify
            assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        }

        @Test
        void placeHeadHLBHeadNull() {
            // prepare

            // execute
            val actualException = Assertions.assertThrows(NullPointerException.class, () ->
                    headPlacer.placeHead((Head) null, new Location(null, 0, 0, 0), BlockFace.NORTH)
            );

            // verify
            assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        }

        @Test
        void placeHeadHLBLocationNull() {
            // prepare

            // execute
            val actualException = Assertions.assertThrows(NullPointerException.class, () ->
                    headPlacer.placeHead(Head.builder().build(), null, BlockFace.NORTH)
            );

            // verify
            assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        }

        @Test
        void placeHeadHLBRotationNull() {
            // prepare

            // execute
            val actualException = Assertions.assertThrows(NullPointerException.class, () ->
                    headPlacer.placeHead(Head.builder().build(), new Location(null, 0, 0, 0), null)
            );

            // verify
            assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        }

        @Test
        void placeWallHeadILBHeadItemStackNull() {
            // prepare

            // execute
            val actualException = Assertions.assertThrows(NullPointerException.class, () ->
                    headPlacer.placeWallHead((ItemStack) null, new Location(null, 0, 0, 0), BlockFace.NORTH)
            );

            // verify
            assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        }

        @Test
        void placeWallHeadILBLocationNull() {
            // prepare

            // execute
            val actualException = Assertions.assertThrows(NullPointerException.class, () ->
                    headPlacer.placeWallHead(new ItemStack(Material.PLAYER_HEAD), null, BlockFace.NORTH)
            );

            // verify
            assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        }

        @Test
        void placeWallHeadILBRotationNull() {
            // prepare

            // execute
            val actualException = Assertions.assertThrows(NullPointerException.class, () ->
                    headPlacer.placeWallHead(new ItemStack(Material.PLAYER_HEAD), new Location(null, 0, 0, 0), null)
            );

            // verify
            assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        }

        @Test
        void placeWallHeadHLBHeadNull() {
            // prepare

            // execute
            val actualException = Assertions.assertThrows(NullPointerException.class, () ->
                    headPlacer.placeWallHead((Head) null, new Location(null, 0, 0, 0), BlockFace.NORTH)
            );

            // verify
            assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        }

        @Test
        void placeWallHeadHLBLocationNull() {
            // prepare

            // execute
            val actualException = Assertions.assertThrows(NullPointerException.class, () ->
                    headPlacer.placeWallHead(Head.builder().build(), null, BlockFace.NORTH)
            );

            // verify
            assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        }

        @Test
        void placeWallHeadHLBRotationNull() {
            // prepare

            // execute
            val actualException = Assertions.assertThrows(NullPointerException.class, () ->
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
            val actualException = Assertions.assertThrows(IllegalArgumentException.class, () ->
                    headPlacer.placeHead(new ItemStack(Material.DIRT), new Location(null, 0, 0, 0), BlockFace.NORTH)
            );

            // verify
            assertThat(actualException.getMessage(), is("The Material of the provided ItemStack is not equal to PLAYER_HEAD"));
        }

        @Test
        void placeWallHeadILBHeadItemStackNotPlayerHead() {
            // prepare

            // execute
            val actualException = Assertions.assertThrows(IllegalArgumentException.class, () ->
                    headPlacer.placeWallHead(new ItemStack(Material.DIRT), new Location(null, 0, 0, 0), BlockFace.NORTH)
            );

            // verify
            assertThat(actualException.getMessage(), is("The Material of the provided ItemStack is not equal to PLAYER_HEAD"));
        }
    }

    @Test
    void placeHeadILB() {
        // prepare
        val testHeadItemStack = mock(ItemStack.class);
        val testWorld = mock(World.class);
        val testLocation = new Location(testWorld, 0, 0, 0);
        val testRotation = BlockFace.NORTH;
        val testBlock = mock(Block.class);
        val testRotatable = mock(Rotatable.class);
        val testNBTItem = mock(NBTItem.class);
        val testSkullOwnerCompound = mock(NBTCompound.class);
        val testNBTTileEntity = mock(NBTTileEntity.class);
        val testNBTContainer = mock(NBTContainer.class);
        val testSkullOwner = "{Id:[I;19088743,-1985229329,-19088744,1985229328],Properties:{textures:[{Value:\"TestValue\"}]},Name:\"TestName\"}";

        when(testHeadItemStack.getType())
                .thenReturn(Material.PLAYER_HEAD);
        when(testWorld.getBlockAt(testLocation))
                .thenReturn(testBlock);
        doNothing().when(testBlock).setType(eq(Material.PLAYER_HEAD));

        when(testBlock.getBlockData())
                .thenReturn(testRotatable);
        doNothing().when(testRotatable).setRotation(testRotation);
        doNothing().when(testBlock).setBlockData(testRotatable);

        when(nbtService.getNbtItem(testHeadItemStack))
                .thenReturn(testNBTItem);
        when(testNBTItem.getCompound("SkullOwner"))
                .thenReturn(testSkullOwnerCompound);
        when(testSkullOwnerCompound.toString())
                .thenReturn(testSkullOwner);

        when(nbtService.getNbtTileEntity(testBlock))
                .thenReturn(testNBTTileEntity);
        when(nbtService.getNbtContainer("{SkullOwner:" + testSkullOwner + "}"))
                .thenReturn(testNBTContainer);
        doNothing().when(testNBTTileEntity).mergeCompound(testNBTContainer);


        // execute
        headPlacer.placeHead(testHeadItemStack, testLocation, testRotation);

        // verify
        verifyNoMoreInteractions(nbtService, headUtils);
        verifyNoMoreInteractions(testHeadItemStack, testWorld, testBlock, testRotatable);
        verifyNoMoreInteractions(testNBTItem, testSkullOwnerCompound, testNBTTileEntity, testNBTContainer);
    }

    @Test
    void placeHeadHLB() {
        // prepare
        val testHead = Head.builder()
                .headOwner(UUID.fromString("01234567-89ab-cdef-fedc-ba9876543210"))
                .name("TestName")
                .value("TestValue")
                .build();
        val testWorld = mock(World.class);
        val testLocation = new Location(testWorld, 0, 0, 0);
        val testRotation = BlockFace.NORTH;
        val testBlock = mock(Block.class);
        val testRotatable = mock(Rotatable.class);
        val testNBTTileEntity = mock(NBTTileEntity.class);
        val testNBTContainer = mock(NBTContainer.class);
        val testSkullOwner = "{Id:[I;19088743,-1985229329,-19088744,1985229328],Properties:{textures:[{Value:\"TestValue\"}]},Name:\"TestName\"}";

        when(testWorld.getBlockAt(testLocation))
                .thenReturn(testBlock);
        doNothing().when(testBlock).setType(eq(Material.PLAYER_HEAD));

        when(testBlock.getBlockData())
                .thenReturn(testRotatable);
        doNothing().when(testRotatable).setRotation(testRotation);
        doNothing().when(testBlock).setBlockData(testRotatable);

        doCallRealMethod().when(headUtils).getIntArrayFromUuid(any());

        when(nbtService.getNbtTileEntity(testBlock))
                .thenReturn(testNBTTileEntity);
        when(nbtService.getNbtContainer("{SkullOwner:" + testSkullOwner + "}"))
                .thenReturn(testNBTContainer);
        doNothing().when(testNBTTileEntity).mergeCompound(testNBTContainer);


        // execute
        headPlacer.placeHead(testHead, testLocation, testRotation);

        // verify
        verifyNoMoreInteractions(nbtService, headUtils);
        verifyNoMoreInteractions(testWorld, testBlock, testRotatable);
        verifyNoMoreInteractions(testNBTTileEntity, testNBTContainer);
    }

    @Test
    void placeWallHeadILB() {
        // prepare
        val testHeadItemStack = mock(ItemStack.class);
        val testWorld = mock(World.class);
        val testLocation = new Location(testWorld, 0, 0, 0);
        val testDirection = BlockFace.NORTH;
        val testBlock = mock(Block.class);
        val testDirectional = mock(Directional.class);
        val testNBTItem = mock(NBTItem.class);
        val testSkullOwnerCompound = mock(NBTCompound.class);
        val testNBTTileEntity = mock(NBTTileEntity.class);
        val testNBTContainer = mock(NBTContainer.class);
        val testSkullOwner = "{Id:[I;19088743,-1985229329,-19088744,1985229328],Properties:{textures:[{Value:\"TestValue\"}]},Name:\"TestName\"}";

        when(testHeadItemStack.getType())
                .thenReturn(Material.PLAYER_HEAD);
        when(testWorld.getBlockAt(testLocation))
                .thenReturn(testBlock);
        doNothing().when(testBlock).setType(eq(Material.PLAYER_WALL_HEAD));

        when(testBlock.getBlockData())
                .thenReturn(testDirectional);
        doNothing().when(testDirectional).setFacing(testDirection);
        doNothing().when(testBlock).setBlockData(testDirectional);

        when(nbtService.getNbtItem(testHeadItemStack))
                .thenReturn(testNBTItem);
        when(testNBTItem.getCompound("SkullOwner"))
                .thenReturn(testSkullOwnerCompound);
        when(testSkullOwnerCompound.toString())
                .thenReturn(testSkullOwner);

        when(nbtService.getNbtTileEntity(testBlock))
                .thenReturn(testNBTTileEntity);
        when(nbtService.getNbtContainer("{SkullOwner:" + testSkullOwner + "}"))
                .thenReturn(testNBTContainer);
        doNothing().when(testNBTTileEntity).mergeCompound(testNBTContainer);


        // execute
        headPlacer.placeWallHead(testHeadItemStack, testLocation, testDirection);

        // verify
        verifyNoMoreInteractions(nbtService, headUtils);
        verifyNoMoreInteractions(testHeadItemStack, testWorld, testBlock, testDirectional);
        verifyNoMoreInteractions(testNBTItem, testSkullOwnerCompound, testNBTTileEntity, testNBTContainer);
    }

    @Test
    void placeWallHeadHLB() {
        // prepare
        val testHead = Head.builder()
                .headOwner(UUID.fromString("01234567-89ab-cdef-fedc-ba9876543210"))
                .name("TestName")
                .value("TestValue")
                .build();
        val testWorld = mock(World.class);
        val testLocation = new Location(testWorld, 0, 0, 0);
        val testDirection = BlockFace.NORTH;
        val testBlock = mock(Block.class);
        val testDirectional = mock(Directional.class);
        val testNBTTileEntity = mock(NBTTileEntity.class);
        val testNBTContainer = mock(NBTContainer.class);
        val testSkullOwner = "{Id:[I;19088743,-1985229329,-19088744,1985229328],Properties:{textures:[{Value:\"TestValue\"}]},Name:\"TestName\"}";

        when(testWorld.getBlockAt(testLocation))
                .thenReturn(testBlock);
        doNothing().when(testBlock).setType(eq(Material.PLAYER_WALL_HEAD));

        when(testBlock.getBlockData())
                .thenReturn(testDirectional);
        doNothing().when(testDirectional).setFacing(testDirection);
        doNothing().when(testBlock).setBlockData(testDirectional);

        doCallRealMethod().when(headUtils).getIntArrayFromUuid(any());

        when(nbtService.getNbtTileEntity(testBlock))
                .thenReturn(testNBTTileEntity);
        when(nbtService.getNbtContainer("{SkullOwner:" + testSkullOwner + "}"))
                .thenReturn(testNBTContainer);
        doNothing().when(testNBTTileEntity).mergeCompound(testNBTContainer);


        // execute
        headPlacer.placeWallHead(testHead, testLocation, testDirection);

        // verify
        verifyNoMoreInteractions(nbtService, headUtils);
        verifyNoMoreInteractions(testWorld, testBlock, testDirectional);
        verifyNoMoreInteractions(testNBTTileEntity, testNBTContainer);
    }
}