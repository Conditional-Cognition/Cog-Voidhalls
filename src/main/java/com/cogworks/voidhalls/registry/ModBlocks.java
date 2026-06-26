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

    private static DeferredBlock<ModTableBlock> registerTable(String name, Block plankBlock) {
        return BLOCKS.register(
                name,
                () -> new ModTableBlock(
                        BlockBehaviour.Properties.ofFullCopy(plankBlock)
                )
        );
    }

    public static final DeferredBlock<ModTableBlock> OAK_TABLE =
            registerTable("oak_table", Blocks.OAK_PLANKS);
    public static final DeferredBlock<ModTableBlock> SPRUCE_TABLE =
            registerTable("spruce_table", Blocks.SPRUCE_PLANKS);
    public static final DeferredBlock<ModTableBlock> BIRCH_TABLE =
            registerTable("birch_table", Blocks.BIRCH_PLANKS);
    public static final DeferredBlock<ModTableBlock> JUNGLE_TABLE =
            registerTable("jungle_table", Blocks.JUNGLE_PLANKS);
    public static final DeferredBlock<ModTableBlock> ACACIA_TABLE =
            registerTable("acacia_table", Blocks.ACACIA_PLANKS);
    public static final DeferredBlock<ModTableBlock> DARK_OAK_TABLE =
            registerTable("dark_oak_table", Blocks.DARK_OAK_PLANKS);
    public static final DeferredBlock<ModTableBlock> MANGROVE_TABLE =
            registerTable("mangrove_table", Blocks.MANGROVE_PLANKS);
    public static final DeferredBlock<ModTableBlock> CHERRY_TABLE =
            registerTable("cherry_table", Blocks.CHERRY_PLANKS);
    public static final DeferredBlock<ModTableBlock> BAMBOO_TABLE =
            registerTable("bamboo_table", Blocks.BAMBOO_PLANKS);
    public static final DeferredBlock<ModTableBlock> CRIMSON_TABLE =
            registerTable("crimson_table", Blocks.CRIMSON_PLANKS);
    public static final DeferredBlock<ModTableBlock> WARPED_TABLE =
            registerTable("warped_table", Blocks.WARPED_PLANKS);

    public static final DeferredBlock<ModTableBlock> DIORITE_TABLE =
            registerTable("diorite_table", Blocks.IRON_TRAPDOOR);
    public static final DeferredBlock<ModTableBlock> ANDESITE_TABLE =
            registerTable("andesite_table", Blocks.IRON_TRAPDOOR);
    public static final DeferredBlock<ModTableBlock> GRANITE_TABLE =
            registerTable("granite_table", Blocks.IRON_TRAPDOOR);
    public static final DeferredBlock<ModTableBlock> STONE_TABLE =
            registerTable("stone_table", Blocks.IRON_TRAPDOOR);

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