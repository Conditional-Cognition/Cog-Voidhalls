package com.cogworks.voidhalls.registry;

import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.cogworks.voidhalls.blocks.*;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks("voidhalls");

    public static final DeferredBlock<Block> TEST_BLOCK =
            BLOCKS.registerSimpleBlock("test_block",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE));

    public static final DeferredHolder<Block, LiquidBlock> TEST_FLUID_BLOCK =
            BLOCKS.register("test_fluid_block", () -> new LiquidBlock(
                    ModFluids.TEST_FLUID_STILL.get(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)
            ));

    public static final DeferredBlock<LayerWallpaperBlock> LAYER_ZERO_WALLPAPER =
            BLOCKS.register("layer_zero_wallpaper", () -> new LayerWallpaperBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.BAMBOO_PLANKS).noOcclusion()
            ));
    public static final DeferredBlock<Block> LAYER_ZERO_WALL =
            BLOCKS.registerSimpleBlock("layer_zero_wall",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE));
    public static final DeferredHolder<Block, StairBlock> LAYER_ZERO_WALL_STAIRS =
        BLOCKS.register("layer_zero_wall_stairs", () -> new StairBlock(
                ModBlocks.LAYER_ZERO_WALL.get().defaultBlockState(),
                BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredHolder<Block, SlabBlock> LAYER_ZERO_WALL_SLAB =
        BLOCKS.register("layer_zero_wall_slab", () -> new SlabBlock(
                BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredBlock<Block> LAYER_ZERO_CEILING_TILE =
            BLOCKS.registerSimpleBlock("layer_zero_ceiling_tile",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.SMOOTH_STONE));
    public static final DeferredBlock<Block> LAYER_ZERO_SCAFFOLD =
            BLOCKS.registerSimpleBlock("layer_zero_scaffold",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.SCAFFOLDING));

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}